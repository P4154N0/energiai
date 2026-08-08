package com.energiai.api.client;

/**
 * LA CARTA DE REPOSTERÍA FINA (Interfaz del Cliente de IA)
 *
 * El menú especializado que define el arte dulce de la casa. Declara que existe un saber hacer
 * capaz de tomar los ingredientes numéricos y entregar una predicción refinada.
 *
 * No le importa si el postre lo elabora el Repostero Titular en su taller distante de Python
 * o el Repostero Ayudante en los fuegos de Java: ambos se comprometen a respetar la misma
 * firma y la misma vajilla.
 */

public interface MlModelClient {

    MlPrediccionResultado predecir(MlPrediccionRequest datos);

}