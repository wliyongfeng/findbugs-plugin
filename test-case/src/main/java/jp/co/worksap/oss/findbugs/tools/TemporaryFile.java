package jp.co.worksap.oss.findbugs.tools;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;

public final class TemporaryFile extends File {
    private static final long serialVersionUID = -7726956038730786387L;
    private final transient Logger logger = LoggerFactory.getLogger(getClass());
    /** Maximum loop count when creating temp directories. */
    private static final transient int TEMP_DIR_ATTEMPTS = 10000;
    private static File baseDirectory;

    static {
        // get temp directory from database or configure file, if no setting for
        // this, get one use Files.Files.createTempDir()
        baseDirectory = Files.createTempDir();
    }

    private TemporaryFile(String child) {
        super(baseDirectory, child);
    }

    @Nonnull
    public static TemporaryFile newTemporaryFile() {
        return new TemporaryFile(getRandomTempFileName());
    }

    @Nonnull
    public static TemporaryFile newTemporaryDir() {
        for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
            TemporaryFile tempDir = newTemporaryFile();
            if (tempDir.mkdir()) {
                return tempDir;
            }
        }
        throw new IllegalStateException("Failed to create directory within "
                + TEMP_DIR_ATTEMPTS + " attempts tried ");
    }

    @Nonnull
    public static TemporaryFile fromInputStream(
            @Nonnull final InputStream inputStream) {
        checkNotNull(inputStream);
        TemporaryFile tempFile = new TemporaryFile(getRandomTempFileName());
        try {
            Files.copy(new InputSupplier<InputStream>() {

                @Override
                public InputStream getInput() throws IOException {
                    return inputStream;
                }

            }, tempFile);
        } catch (IOException e) {
            throw new IllegalStateException("can not copy InputStream to File",
                    e);
        }
        return tempFile;
    }

    @Nonnull
    public static TemporaryFile fromResource(@Nonnull String resourcePath) {
        URL resource = Resources.getResource(resourcePath);
        TemporaryFile tempFile = new TemporaryFile(getRandomTempFileName());
        try {
            Files.copy(Resources.newInputStreamSupplier(resource), tempFile);
        } catch (IOException e) {
            throw new IllegalStateException("can not copy Resource to File", e);
        }
        return tempFile;
    }

    @Nonnull
    public FileInputStream getFileRemoveInputStream() {
        if (this.isDirectory()) {
            throw new IllegalStateException(
                    "Can not get FileRemoveInputStream from a directory");
        }
        try {
            return new FileRemoveInputStream(this);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("fiile does not exist", e);
        }
    }

    private static class FileRemoveInputStream extends FileInputStream {
        private final Logger logger = LoggerFactory.getLogger(getClass());
        private final File file;

        public FileRemoveInputStream(File file) throws FileNotFoundException {
            super(file);
            this.file = file;
        }

        public void close() throws IOException {
            super.close();
            if (!file.delete()) {
                logger.warn("Can not delete temp csv file: {}",
                        file.getAbsolutePath());
            }
        }
    }

    private static String getRandomTempFileName() {
        // we use version 4 UUID to enhance the randomness while version 1
        // can make sure the uniqueness but not secure(use MAC address)
        return UUID.randomUUID().toString();
    }

    public void deleteQuietly() {
        deleteFile(this);
    }

    private void deleteFile(File file) {
        if (!file.exists()) {
            logger.warn("the file [{}] does not exist", file.getAbsolutePath());
            return;
        }

        if (file.isFile()) {
            if (!file.delete()) {
                logger.warn("can not delete the directory {}",
                        file.getAbsolutePath());
            }
            return;
        }

        for (File child : file.listFiles()) {
            deleteFile(child);
        }

        // delete the empty directory
        if (!file.delete()) {
            logger.warn("can not delete the directory {}",
                    file.getAbsolutePath());
        }
    }

}
