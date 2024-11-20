package org.fogbeam.example.opennlp;

import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * @file TokenizerMain.java
 * @brief Programa para tokenizar texto de archivos utilizando Apache OpenNLP.
 *
 * Este programa lee archivos de texto en inglés desde una carpeta especificada,
 * tokeniza su contenido utilizando un modelo preentrenado y genera un archivo de
 * salida con el texto separado por tokens.
 *
 * @author TuNombre
 * @date 2024-11-20
 */
public class TokenizerMain {

	/**
	 * @brief Método principal de la aplicación.
	 *
	 * Este método realiza las siguientes acciones:
	 * - Verifica los argumentos de entrada.
	 * - Carga el modelo de tokenización entrenado.
	 * - Procesa todos los archivos de texto en el directorio especificado.
	 * - Escribe los tokens en un archivo de salida.
	 *
	 * @param args Argumentos de entrada:
	 *             - args[0]: Carpeta de entrada con archivos `.txt`.
	 *             - args[1]: Archivo de salida donde se escribirá el texto tokenizado.
	 * @throws Exception Si ocurre algún error durante la ejecución.
	 */
	public static void main(String[] args) throws Exception {
		// Verificar que se proporcionen argumentos de entrada y salida
		if (args.length < 2) {
			System.out.println("Usage: java TokenizerMain <input_folder> <output_file>");
			return;
		}

		String inputFolder = args[0];
		String outputFile = args[1];

		// Ruta al modelo de tokenización entrenado
		try (InputStream modelIn = new FileInputStream("models/en-token.model")) {
			// Cargar el modelo de tokenización
			TokenizerModel model = new TokenizerModel(modelIn);
			Tokenizer tokenizer = new TokenizerME(model);

			// Crear el archivo de salida
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
				// Leer todos los archivos de texto del directorio de entrada
				Files.walk(Paths.get(inputFolder))
						.filter(Files::isRegularFile) // Solo archivos
						.filter(path -> path.toString().endsWith(".txt")) // Solo archivos .txt
						.forEach(file -> processFile(file, tokenizer, writer));
			}

			System.out.println("Tokenization complete. Output written to: " + outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @brief Procesa un archivo de texto y escribe los tokens en el archivo de salida.
	 *
	 * Este método lee el contenido de un archivo, lo tokeniza utilizando el modelo cargado
	 * y escribe los tokens resultantes en el archivo de salida especificado.
	 *
	 * @param file Archivo de entrada a procesar.
	 * @param tokenizer Instancia del tokenizador configurado con el modelo.
	 * @param writer Escritor para el archivo de salida.
	 */
	private static void processFile(String filePath, Tokenizer tokenizer, BufferedWriter writer) throws IOException {
		// Leer el contenido del archivo
		String content = new String(Files.readAllBytes(Paths.get(filePath)));

		// Tokenizar el contenido
		String[] tokens = tokenizer.tokenize(content);

		// Escribir los tokens en el archivo de salida
		for (String token : tokens) {
			writer.write(token);
			writer.write(" "); // Separar tokens por espacio
		}
		writer.newLine(); // Separar por línea cada archivo procesado

		System.out.println("Processed file: " + filePath);
	}
}
