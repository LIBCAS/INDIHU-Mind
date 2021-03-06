package cz.cas.lib.indihumind.service;

import com.openhtmltopdf.extend.FSTextBreaker;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import core.exception.GeneralException;
import cz.cas.lib.indihumind.citationtemplate.Typeface;
import j2html.Config;
import j2html.TagCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.BreakIterator;
import java.util.List;
import java.util.Locale;

import static j2html.TagCreator.*;

@Slf4j
@Service
public class PdfExporter {

    private static final String FONT_STYLESHEET_URL = "https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,400;0,700;1,400;1,700&amp;display=swap";
    private static final String FONT_NAME = "Roboto";
    private static final String FONT_FALLBACK_NAME = "sans-serif";

    /**
     * Creates a PDF from generated HTML.
     *
     * @return pdf in byte array
     * @implNote Explicit font must be provided to generate czech-specific letters like {@code ůčěščřžŮČĚŠČŘŽ}.
     *         Easiest way to provide it is to add external stylesheet to the HTML and reference it.
     *         For some arcane magic reason I was unable to load fonts from files in resources.
     *         Neither builder.useFont() nor @font-face with URL declaration in HTML style section had worked.
     */
    public byte[] export(List<String> linesWithHtmlTagging) {
        String htmlPage = createHtml(linesWithHtmlTagging);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.toStream(out)
                    .defaultTextDirection(PdfRendererBuilder.TextDirection.LTR)
                    .withHtmlContent(htmlPage, ".")
                    .useUnicodeLineBreaker(new FSTextBreaker() {
                        final BreakIterator br = BreakIterator.getLineInstance(Locale.forLanguageTag("cs-CZ"));

                        @Override
                        public int next() {
                            return br.next();
                        }

                        @Override
                        public void setText(String newText) {
                            br.setText(newText);
                        }
                    });

            builder.run();
            log.debug("PDF successfully generated.");

            return out.toByteArray();

        } catch (IOException ex) {
            throw new GeneralException("PDF stream has malfunctioned");
        }
    }

    /**
     * Generates HTML with tagged values from records.
     *
     * @param lines - tagged values generated by {@link Typeface#formatData}
     * @return HTML with font for czech letters
     */
    private String createHtml(List<String> lines) {
        Config.textEscaper = text -> text;
        Config.closeEmptyTags = true;
        return document(html(
                head(
                        meta().withCharset(StandardCharsets.UTF_8.name()),
                        link().withHref(FONT_STYLESHEET_URL).withRel("stylesheet"),
                        style(String.format("body {font-family: '%s', %s;}", FONT_NAME, FONT_FALLBACK_NAME))
                ),
                body(div(each(lines, TagCreator::p)))));
    }

}
