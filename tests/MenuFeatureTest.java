import dao.MenuDAO;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.ElementoMenu;
import model.MenuRestaurante;
import service.FiltroMenu;

/** Pruebas ejecutables con JDK 21, sin credenciales ni dependencias externas. */
public final class MenuFeatureTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        List<MenuRestaurante> source = List.of(
                new MenuRestaurante(1, "Alpha", "CIT", List.of(
                        item(1, "Tortilla", "Extra", "Comida", "2.00"),
                        item(2, "Café frío", "Café", "Bebida", "30.00"),
                        item(3, "Pollo al horno", "Almuerzo", "Comida", "40.00"))),
                new MenuRestaurante(2, "Beta", "Patio", List.of(
                        item(4, "Jugo", "Bebida", "Fría", "10.00"),
                        item(5, "Pollo", "Almuerzo", "Comida", "25.00"),
                        item(6, "Café latte", "Café", "Bebida", "20.00"))),
                new MenuRestaurante(3, "Gamma", "F", List.of(
                        item(7, "Bebida especial", "Bebida", "Fría", null))),
                new MenuRestaurante(4, "Sin menú", "G", List.of()));

        var all = search(source, "", null, null, true);
        eq(names(all), List.of("Alpha", "Beta", "Gamma", "Sin menú"), "Orden por mínimo y nulos al final");
        eq(ids(all.get(0)), List.of(1L, 2L, 3L), "Productos de menor a mayor precio");
        eq(all.size(), 4, "Restaurante sin menú visible al explorar");
        var drinks = search(source, "BEBIDA", null, null, true);
        eq(names(drinks), List.of("Beta", "Alpha", "Gamma"), "Mínimo después de buscar y tipo_2 incluido");
        eq(ids(drinks.get(0)), List.of(4L, 6L), "Tipo_1 y tipo_2 se buscan por igual");
        eq(ids(drinks.get(1)), List.of(2L), "No incluye extras ajenos a la búsqueda");
        eq(search(source, "cafe", null, null, true).stream().mapToInt(m -> m.elementos().size()).sum(), 2,
                "Búsqueda sin tildes");
        eq(ids(search(source, " CAFÉ  FRÍO ", null, null, true).get(0)), List.of(2L), "Varias palabras y espacios");
        var filtered = search(source, "Bebida", "10", "20", true);
        eq(names(filtered), List.of("Beta"), "Texto combinado con rango inclusivo");
        eq(ids(filtered.get(0)), List.of(4L, 6L), "Límites exactos incluidos");
        eq(names(search(source, "pollo", null, "30", true)), List.of("Beta"), "Nombre y presupuesto máximo");
        eq(names(search(source, "bebida", "30", null, true)), List.of("Alpha"), "Mínimo sin máximo");
        eq(search(source, "inexistente", null, null, true).size(), 0, "No hay grupos vacíos al buscar");
        eq(search(source, "", null, "0", true).size(), 0, "Null no se convierte a Q0");
        var zero = List.of(new MenuRestaurante(9, "Gratis", "", List.of(item(8,"Agua","Bebida","Fría","0"))));
        eq(search(zero, "", "0", "0", true).size(), 1, "Precio real de Q0 sí se conserva");
        var alpha = search(source, "Bebida", null, null, false);
        eq(names(alpha), List.of("Alpha", "Beta", "Gamma"), "Orden alfabético opcional");
        eq(ids(alpha.get(1)), List.of(6L, 4L), "Productos alfabéticos sin cambiar filtros");
        expectInvalid(() -> search(source, "", "-1", null, true), "Rechaza negativos");
        expectInvalid(() -> search(source, "", "31", "10", true), "Rechaza rango invertido");
        eq(source.get(1).elementos().get(0).id(), 4L, "No cambia la lista de origen");
        eq(FiltroMenu.precioMasBajo(source.get(2)), null, "Mínimo nulo sin precios");
        // Regla de la vista: menús cerrados sin filtros; abiertos y fijos con nombre o precio.
        eq(FiltroMenu.hayFiltros("", null, null), false, "Sin búsqueda ni precio: menús cerrados");
        eq(FiltroMenu.hayFiltros("   ", null, null), false, "Solo espacios no es un filtro");
        eq(FiltroMenu.hayFiltros(null, null, null), false, "Texto nulo no es un filtro");
        eq(FiltroMenu.hayFiltros("Pollo", null, null), true, "Búsqueda por nombre abre los menús");
        eq(FiltroMenu.hayFiltros("", new BigDecimal("0"), null), true, "Mínimo Q0 sí es un filtro");
        eq(FiltroMenu.hayFiltros("", null, new BigDecimal("30")), true, "Máximo abre los menús");
        eq(FiltroMenu.hayFiltros("café", new BigDecimal("5"), new BigDecimal("30")), true, "Nombre y precio juntos");
        checkDao();
        System.out.println("OK: " + checks + " comprobaciones de búsqueda, filtros y lectura SQL.");
    }

    private static void checkDao() throws Exception {
        List<Map<String,Object>> rows = new ArrayList<>();
        rows.add(row(7, "&Cafe", 31L, "Latte", "Café", "Bebida", new BigDecimal("26.50")));
        rows.add(row(7, "&Cafe", 32L, "Agotado", "Comida", "Sandwich", null));
        rows.add(row(9, "Gitane", null, null, null, null, null));
        int[] cursor = {-1}; boolean[] wasNull = {false}; boolean[] closed = new boolean[3];
        ResultSet rs = (ResultSet) Proxy.newProxyInstance(MenuFeatureTest.class.getClassLoader(),
                new Class<?>[]{ResultSet.class}, (proxy, method, args) -> {
                    String n = method.getName();
                    if (n.equals("next")) return ++cursor[0] < rows.size();
                    if (n.equals("close")) { closed[0]=true; return null; }
                    if (n.equals("wasNull")) return wasNull[0];
                    if (List.of("getString","getInt","getLong","getBigDecimal").contains(n)) {
                        Object value = rows.get(cursor[0]).get(args[0]); wasNull[0] = value == null;
                        return switch(n) {
                            case "getInt" -> value == null ? 0 : ((Number)value).intValue();
                            case "getLong" -> value == null ? 0L : ((Number)value).longValue();
                            default -> value;
                        };
                    }
                    throw new UnsupportedOperationException(n);
                });
        PreparedStatement st = (PreparedStatement) Proxy.newProxyInstance(MenuFeatureTest.class.getClassLoader(),
                new Class<?>[]{PreparedStatement.class}, (proxy, method, args) -> switch(method.getName()) {
                    case "executeQuery" -> rs;
                    case "setQueryTimeout" -> { eq(args[0],20,"Timeout de consulta"); yield null; }
                    case "close" -> { closed[1]=true; yield null; }
                    default -> throw new UnsupportedOperationException(method.getName());
                });
        Connection cn = (Connection) Proxy.newProxyInstance(MenuFeatureTest.class.getClassLoader(),
                new Class<?>[]{Connection.class}, (proxy, method, args) -> switch(method.getName()) {
                    case "prepareStatement" -> {
                        eq(((String)args[0]).contains("LEFT JOIN menu_elemento"),true,"Incluye restaurantes sin menú");
                        yield st;
                    }
                    case "close" -> { closed[2]=true; yield null; }
                    default -> throw new UnsupportedOperationException(method.getName());
                });
        var data = new MenuDAO().obtenerMenus(cn);
        eq(data.size(),2,"Agrupa por id de restaurante");
        eq(data.get(0).nombre(),"& Café","Presentación de & Café");
        eq(data.get(0).idRestaurante(),7,"Conserva id real");
        eq(data.get(0).elementos().get(0).precio(),new BigDecimal("26.50"),"Precio decimal exacto");
        eq(data.get(0).elementos().get(1).precio(),null,"Precio SQL NULL conservado");
        eq(data.get(1).elementos().size(),0,"LEFT JOIN no fabrica un producto vacío");
        eq(Arrays.toString(closed),"[true, true, false]","Cierra statement/resultado sin cerrar conexión ajena");
        Connection failed = (Connection) Proxy.newProxyInstance(MenuFeatureTest.class.getClassLoader(),
                new Class<?>[]{Connection.class}, (p,m,a) -> { throw new SQLException("Error simulado"); });
        try { new MenuDAO().obtenerMenus(failed); throw new AssertionError("Ocultó el fallo SQL"); }
        catch (SQLException expected) { checks++; }
    }

    private static Map<String,Object> row(int id, String restaurant, Long itemId, String name,
            String type1, String type2, BigDecimal price) {
        Map<String,Object> row=new HashMap<>();
        row.put("id_restaurante",id); row.put("restaurante",restaurant); row.put("ubicacion","CIT");
        row.put("id_elemento_menu",itemId); row.put("elemento",name); row.put("tipo_1",type1);
        row.put("tipo_2",type2); row.put("precio",price); return row;
    }
    private static ElementoMenu item(long id,String name,String t1,String t2,String price) {
        return new ElementoMenu(id,name,t1,t2,price==null?null:new BigDecimal(price));
    }
    private static List<MenuRestaurante> search(List<MenuRestaurante> menus,String q,String min,String max,boolean sort) {
        return FiltroMenu.aplicar(menus,q,min==null?null:new BigDecimal(min),max==null?null:new BigDecimal(max),sort);
    }
    private static List<String> names(List<MenuRestaurante> list) {return list.stream().map(MenuRestaurante::nombre).toList();}
    private static List<Long> ids(MenuRestaurante menu) {return menu.elementos().stream().map(ElementoMenu::id).toList();}
    private static void eq(Object actual,Object expected,String label) {
        if(!java.util.Objects.equals(actual,expected))throw new AssertionError(label+": "+actual+" != "+expected);
        checks++;
    }
    private static void expectInvalid(Runnable action,String label) {
        try {action.run();throw new AssertionError(label);} catch(IllegalArgumentException expected){checks++;}
    }
}
