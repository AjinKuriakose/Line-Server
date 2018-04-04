package com.ak.app.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import com.ak.app.cache.LRUConcurrentCache;

/**

 */
@Component
public class LineServer {
	private static File inputFile = null;
	private RandomAccessFile inputRandomAccessFile = null;
	private static File indexFile = null;
	private RandomAccessFile indexRandomAccessFile = null;
	private static final Integer MAX_CAPACITY = 1000;
	public static final String FILE_NAME = "file_name";
	private LRUConcurrentCache cache = null;
	Long len = 0L;

	public LineServer() {
		cache = new LRUConcurrentCache(MAX_CAPACITY);
	}

	@PostConstruct
	public void init() {
		String inputFileName = System.getProperty(FILE_NAME);
		System.out.println("File to be processed " + inputFileName);
		inputFile = new File(inputFileName);
		int readIndex = 0;
		if (inputFileName.lastIndexOf('/') != -1)
			readIndex = inputFileName.lastIndexOf('/') + 1;
		String indexFileName = "index_" + inputFileName.substring(readIndex);
		indexFile = new File(indexFileName);
		if (indexFile.exists() && indexFile.isFile()) {
			indexFile.delete();
		}
		createIndex();
	}

	/**
	 * Method executed only during the start of the server.Scans the input file and
	 * creates an index file containing offsets for each line in the input
	 * file.Random Access File objects allow us to access these offsets directly in
	 * the input file during a the getLinefromIndex() method call. Initially the
	 * cache is also populated to its capacity.
	 */
	private void createIndex() {
		try {
			inputRandomAccessFile = new RandomAccessFile(inputFile, "r");
			indexRandomAccessFile = new RandomAccessFile(indexFile, "rw");
			len = inputRandomAccessFile.length();

			int lineNumberCount = 1;

			while (inputRandomAccessFile.getFilePointer() < len) {
				Long ptr = inputRandomAccessFile.getFilePointer();
				String currLine = inputRandomAccessFile.readLine();
				cache.put(lineNumberCount++, currLine);
				indexRandomAccessFile.writeLong(ptr);
				indexRandomAccessFile.seek(indexRandomAccessFile.length());
			}
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	/**
	 * Gets the line in the file corresponding to the given line number.The method
	 * first checks whether the requested line is already cached. If not, it finds
	 * the corresponding line using the offset specified in the index file. The
	 * retrieved line is then put in the LRU cache.
	 * @throws IOException 
	 */
	public String getLinefromIndex(int n) throws IOException {
		String line = null;
		if (n < 1 || (len > 0 && n > len))
			throw new IndexOutOfBoundsException();
		if (cache.get(n) != null) {
			//System.out.println("Cache Hit");
			return cache.get(n);
		}
		if (inputRandomAccessFile != null && indexRandomAccessFile != null) {
			long indexPos = (long) ((n - 1) * 8);
			indexRandomAccessFile.seek(indexPos);

			long readPos = indexRandomAccessFile.readLong();
			inputRandomAccessFile.seek(readPos);

			line = inputRandomAccessFile.readLine();
		}
		if (line != null)
			cache.put(n, line);
		return line;
	}

	/**
	 * The RandomAccess Handles are defined global. Cleanup during the spring
	 * container shutdown.
	 * 
	 * @throws IOException
	 */
	@PreDestroy
	public void cleanUp() throws IOException {
		System.out.println("Spring Container Shutdown. Performing Cleanup....");
		if (inputRandomAccessFile != null) {
			inputRandomAccessFile.close();
		}
		if (indexRandomAccessFile != null) {
			indexRandomAccessFile.close();
		}
	}
}