import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BSTTest {

    private BST<Integer> bst;

    @BeforeEach
    public void setUp() {
        bst = new BST<>();
    }

    // ==========================================
    // 1. Pruebas para INSERT
    // ==========================================
    @Test
    public void testInsertBaseYExtremos() {
        // [Base] Insertar en árbol vacío
        bst.insert(10);
        assertTrue(bst.search(10), "El 10 debe ser la raíz");

        // [Base] Insertar menor (izq) y mayor (der)
        bst.insert(5);
        bst.insert(15);
        assertTrue(bst.search(5) && bst.search(15), "5 a la izquierda y 15 a la derecha");

        // [Límite] Duplicados: Insertar la raíz y una hoja nuevamente
        bst.insert(10); 
        bst.insert(5);
        assertEquals(2, bst.countLeaves(), "Los duplicados deben ser ignorados, las hojas no cambian");
    }

    @Test
    public void testInsertArbolDegenerado() {
        // [Extremo] Insertar datos ya ordenados
        for (int i = 1; i <= 5; i++) {
            bst.insert(i);
        }
        // Debería comportarse como una lista (1->2->3->4->5)
        assertEquals(4, bst.height(), "Un árbol degenerado de 5 nodos debe tener altura 4");
        assertEquals(1, bst.width(), "El ancho de un árbol degenerado siempre es 1");
    }

    @Test
    public void testInsertErrores() {
        // [Error] Insertar null
        // Comparamos si el método lanza una excepción al intentar comparar un nulo
        assertThrows(NullPointerException.class, () -> {
            bst.insert(null);
        }, "Insertar null debe lanzar NullPointerException");
    }

    // ==========================================
    // 2. Pruebas para SEARCH
    // ==========================================
    @Test
    public void testSearchBaseYExtremos() {
        // [Base] Buscar en árbol vacío
        assertFalse(bst.search(10), "Árbol vacío no debe encontrar nada");

        bst.insert(10); // Raíz
        bst.insert(5);  // Hoja
        
        // [Límite] Buscar la raíz y una hoja
        assertTrue(bst.search(10), "Debe poder encontrar la raíz");
        assertTrue(bst.search(5), "Debe poder encontrar una hoja profunda");

        // [Base] Buscar algo que no existe
        assertFalse(bst.search(99), "No debe encontrar el 99");
    }

    @Test
    public void testSearchErrores() {
        bst.insert(10);
        // [Error] Buscar null
        assertThrows(NullPointerException.class, () -> {
            bst.search(null);
        }, "Buscar null debe lanzar NullPointerException");
    }

    // ==========================================
    // 3. Pruebas para DELETE (La más crítica)
    // ==========================================
    @Test
    public void testDeleteBase() {
        bst.insert(10); bst.insert(5); bst.insert(15);
        
        // [Base] Eliminar una hoja
        bst.delete(5);
        assertFalse(bst.search(5), "La hoja 5 debe desaparecer");

        // [Base] Eliminar nodo con 1 hijo
        bst.insert(15); bst.insert(20); // 15 ahora tiene hijo 20
        bst.delete(15);
        assertFalse(bst.search(15), "15 eliminado");
        assertTrue(bst.search(20), "El hijo 20 debe subir y sobrevivir");
    }

    @Test
    public void testDeleteExtremosRaizYUnico() {
        // [Extremo] Eliminar el único nodo (la raíz solitaria)
        bst.insert(10);
        bst.delete(10);
        assertFalse(bst.search(10), "El árbol debe quedar vacío");
        assertEquals(-1, bst.height(), "La altura vuelve a -1");

        // [Extremo] Eliminar la raíz con 2 hijos
        bst.insert(10); bst.insert(5); bst.insert(15);
        bst.delete(10);
        assertFalse(bst.search(10), "Raíz 10 eliminada");
        assertTrue(bst.search(5) && bst.search(15), "Sus dos hijos sobreviven");
    }

    @Test
    public void testDeleteExtremoSucesorComplejo() {
        // [Extremo] Nodo con 2 hijos donde su sucesor también tiene hijos
        bst.insert(10); // Nodo a eliminar (tiene izq y der)
        bst.insert(5);
        bst.insert(20); 
        bst.insert(15); // Sucesor de 10 (el más pequeño de los mayores)
        bst.insert(25);
        bst.insert(18); // El sucesor (15) tiene un hijo (18)

        bst.delete(10); 
        
        assertFalse(bst.search(10), "El 10 fue eliminado");
        assertTrue(bst.search(15), "El 15 debió tomar el lugar del 10");
        assertTrue(bst.search(18), "El hijo del sucesor (18) debe reconectarse sin perderse");
    }

    @Test
    public void testDeleteErrores() {
        // [Error] Eliminar en árbol vacío
        // No debe lanzar excepción, solo debe no hacer nada
        assertDoesNotThrow(() -> bst.delete(10), "Eliminar en árbol vacío no debe romper el programa");

        bst.insert(5);
        // [Error] Eliminar inexistente
        assertDoesNotThrow(() -> bst.delete(99), "Eliminar nodo que no existe debe ser ignorado sin fallar");

        // [Error] Eliminar null
        assertThrows(NullPointerException.class, () -> bst.delete(null), "Eliminar null debe lanzar excepción");
    }

    // ==========================================
    // 4. Pruebas para HEIGHT
    // ==========================================
    @Test
    public void testHeightExtremos() {
        // [Límite] Árbol vacío y un nodo
        assertEquals(-1, bst.height(), "Árbol vacío = -1");
        bst.insert(10);
        assertEquals(0, bst.height(), "Un solo nodo = 0");

        // [Extremo] Árbol perfectamente balanceado
        bst.insert(5); bst.insert(15); bst.insert(3); bst.insert(7);
        assertEquals(2, bst.height(), "Árbol balanceado de 5 nodos en 3 niveles (altura 2)");

        // [Extremo] Árbol degenerado (reiniciamos el árbol)
        setUp();
        bst.insert(1); bst.insert(2); bst.insert(3); bst.insert(4);
        assertEquals(3, bst.height(), "Árbol degenerado de 4 nodos tiene altura 3");
    }

    // ==========================================
    // 5. Pruebas para WIDTH
    // ==========================================
    @Test
    public void testWidthExtremos() {
        // [Límite] Árbol vacío y un nodo
        assertEquals(0, bst.width(), "Árbol vacío = ancho 0");
        bst.insert(10);
        assertEquals(1, bst.width(), "Un solo nodo = ancho 1");

        // [Base] Múltiples nodos
        bst.insert(5); bst.insert(15); bst.insert(3); bst.insert(7);
        assertEquals(2, bst.width(), "El nivel más ancho tiene 2 nodos");

        // [Extremo] Árbol degenerado
        setUp();
        bst.insert(1); bst.insert(2); bst.insert(3); bst.insert(4);
        assertEquals(1, bst.width(), "Un árbol degenerado siempre tiene ancho 1");
    }

    // ==========================================
    // 6. Pruebas para COUNT LEAVES
    // ==========================================
    @Test
    public void testCountLeavesExtremos() {
        // [Límite] Árbol vacío y un nodo
        assertEquals(0, bst.countLeaves(), "Árbol vacío = 0 hojas");
        bst.insert(10);
        assertEquals(1, bst.countLeaves(), "La raíz sola es 1 hoja");

        // [Base] Ramificaciones
        bst.insert(5); bst.insert(15); bst.insert(3);
        assertEquals(2, bst.countLeaves(), "Hojas actuales: 3 y 15");

        // [Extremo] Árbol degenerado
        setUp();
        bst.insert(1); bst.insert(2); bst.insert(3); bst.insert(4);
        assertEquals(1, bst.countLeaves(), "El árbol degenerado solo tiene 1 hoja (el último nodo)");
    }
}