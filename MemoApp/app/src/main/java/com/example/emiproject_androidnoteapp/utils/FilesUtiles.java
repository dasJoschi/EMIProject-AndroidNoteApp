package com.example.emiproject_androidnoteapp.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.example.emiproject_androidnoteapp.models.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class FilesUtiles {

    public static void copyFile(String src, File dst) throws IOException {
        copyFile(new FileInputStream(src), dst);
    }

    public static void copyFile(File src, File dst) throws IOException {
        copyFile(new FileInputStream(src), dst);
    }

    public static void copyFile(FileInputStream inStream, File dst) throws IOException {
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    public static void saveToFile(InputStream inputStream, File dst) {
        OutputStream outputStream = null;

        try {
            // write the inputStream to a FileOutputStream
            outputStream = new FileOutputStream(dst);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            System.out.println("Done!");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    public static File createNewFile(Context context, Document document) throws IOException {

        return createNewFile(context, document.getMimeType(),
                MimeTypeUtils.getExtension(document), new Date());
    }

    public static File createNewFile(Context context, @NonNull String mimeType, Date creationDate) throws IOException {

        return createNewFile(context, mimeType,
                MimeTypeUtils.getExtensionFromMimeType(mimeType), creationDate);

    }

    public static File createNewFile(Context context, @NonNull String mimeType, String suffix, Date creationDate) throws IOException {

        String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(creationDate);
        //String imageFileName = "DOCUMENT_" + timeStamp + "_";
        String fileName = mimeType.replace('/', '_').toUpperCase() + "_" + timeStamp + "_";

        File appDir = context.getExternalFilesDir("Documents");
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                fileName,  // prefix
                suffix,    //suffix
                appDir     // directory
        );

    }

    public static File createNewFile(Context context, @NonNull String mimeType) throws IOException {
        return createNewFile(context, mimeType, new Date());
    }

    public static File createTempFile(Context context, String fileName, String mimeType) throws IOException {
        File outputDir = context.getCacheDir(); // context being the Activity pointer

        return File.createTempFile(fileName, MimeTypeUtils.getExtensionFromMimeType(mimeType), outputDir);
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        return file.exists() && file.delete();
    }
}
