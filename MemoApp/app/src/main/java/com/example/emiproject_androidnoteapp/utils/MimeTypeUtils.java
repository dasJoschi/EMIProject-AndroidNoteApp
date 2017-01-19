package com.example.emiproject_androidnoteapp.utils;

import android.webkit.MimeTypeMap;

import com.example.emiproject_androidnoteapp.models.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class MimeTypeUtils {

    private static Map<String, String> extensionsMap = new HashMap<>();

    public static final String AUDIO_MP4 = "audio/mp4";
    public static final String THREE_GPP = "video/3gpp";
    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String IMAGE_PNG = "image/png";
    public static final String PDF = "application/pdf";

    static {
        extensionsMap.put(AUDIO_MP4, "mp4");
        extensionsMap.put(THREE_GPP, "3gp");
        extensionsMap.put(IMAGE_JPEG, "jpg");
        extensionsMap.put(IMAGE_PNG, "png");
        extensionsMap.put(PDF, "pdf");
    }

    public static String getExtensionFromMimeType(String mimeType) {

        String ext = extensionsMap.get(mimeType);
        if (ext == null) {
            ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }

        return ext != null ? "." + ext : null;
    }


    public static String getExtensionFromFileName(String fileName) {

        int dotPos = fileName.lastIndexOf('.');
        if (0 <= dotPos) {
            return fileName.substring(dotPos);
        }

        return null;
    }

    public static String getExtension(Document document) {
        return getExtensionFromMimeType(document.getMimeType());
    }

    public static void registerNewExtension(String ext, String mimeType) {
        extensionsMap.put(ext, mimeType);
    }

    public static boolean isAudio(String mimeType) {
        return mimeType.contains("audio") || mimeType.equals(THREE_GPP);
    }

    public static boolean isImage(String mimeType) {
        return mimeType.contains("image");
    }

    public static boolean isPdf(String mimeType) {
        return mimeType.equals(PDF) || mimeType.contains("pdf");
    }
}
