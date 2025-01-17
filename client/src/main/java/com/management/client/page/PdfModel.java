package com.management.client.page;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.logging.Logger;

public class PdfModel {
    private static final Logger logger = Logger.getLogger(PdfModel.class.getName());
    private PDDocument document;
    private PDFRenderer renderer;

    PdfModel(Path path) {
        try {
            document = PDDocument.load(path.toFile());
            renderer = new PDFRenderer(document);
        } catch (IOException ex) {
            throw new UncheckedIOException("PDFDocument throws IOException file=" + path, ex);
        }
    }
    public PdfModel(byte[] data) {
        try {
            document = PDDocument.load(data);
            renderer = new PDFRenderer(document);
        } catch (IOException ex) {
            throw new UncheckedIOException("PDFDocument throws IOException file=" , ex);
        }
    }

    public int numPages() {
        return document.getPages().getCount();
    }

    public Image getImage(int pageNumber) {
        BufferedImage pageImage;
        try {
            pageImage = renderer.renderImage(pageNumber,0.9f);
        } catch (IOException ex) {
            throw new UncheckedIOException("PDFRenderer throws IOException", ex);
        }
        return SwingFXUtils.toFXImage(pageImage, null);
    }
}