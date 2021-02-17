package cz.cas.lib.indihumind.init.providers;

import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.citation.CitationStore;
import cz.cas.lib.indihumind.citation.Datafield;
import cz.cas.lib.indihumind.citation.Subfield;
import cz.cas.lib.indihumind.security.user.User;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static core.util.Utils.asList;
import static cz.cas.lib.indihumind.util.IndihuMindUtils.AUTHOR_NAME_ENCODING;

@Getter
@Component
public class CitationTestData implements TestDataRemovable {

    @Inject private CitationStore recordStore;

    public Citation recordMovieMLissUsa(User owner) {
        Datafield df1 = new Datafield("245", 'a', "M'liss");

        Datafield df2 = new Datafield("300", '#', '#',
                asList(
                        new Subfield('a', "5 reels of 5 on 2 (1988 ft.)"),
                        new Subfield('b', "si., b&w"),
                        new Subfield('c', "16 mm.")
                ));

        Datafield df3 = new Datafield("110", 'a', "Artcraft Pictures Corporation");

        Datafield df4 = new Datafield("700", 'a', "Neilan" + AUTHOR_NAME_ENCODING + "Marshall A.");
        Datafield df5 = new Datafield("700", 'a', "Marion" + AUTHOR_NAME_ENCODING + "Frances");

        Datafield df6 = new Datafield("710", 'a', "Pickford Film Corp");
        Datafield df7 = new Datafield("710", 'a', "Famous Players-Lasky Corporation");


        Citation record = new Citation();
        record.setOwner(owner);
        record.setId("e7a8c70-1049-11ea-9a9f-362b9e155667");
        record.setName("Film M'liss, 1936 - Spojené státy americké");
        record.setDataFields(asList(df1, df2, df3, df4, df5, df6, df7));
        return  recordStore.save(record);
    }

    public Citation recordChabonMichaelSummerland(User owner) {
        Datafield df1 = new Datafield("020", 'a', "0786808772");

        Datafield df2 = new Datafield("100", 'a', "Chabon" + AUTHOR_NAME_ENCODING + "Mišo");
        Datafield df3 = new Datafield("245", 'a', "Summerland");

        Datafield df4 = new Datafield("250", 'a', "1st ed.");

        Datafield df5 = new Datafield("264", '#', '#',
                asList(
                        new Subfield('a', "New York"),
                        new Subfield('b', "Miramax Knižky/Hyperion Books for Children"),
                        new Subfield('c', "2002")
                ));

        Datafield df6 = new Datafield("300", '#', '#',
                asList(
                        new Subfield('a', "500"),
                        new Subfield('c', "22 cm")
                ));

        Citation record = new Citation();
        record.setOwner(owner);
        record.setId("e635e4ae-081a-4ba4-b099-d7788471c984");
        record.setName("Chabon, Michael: SummerLand, ISBN: 0786808772");
        record.setDataFields(asList(df1, df2, df3, df4, df5, df6));
        return  recordStore.save(record);

    }

    public Citation recordIsbn(User owner) {
        Citation record = new Citation();
        record.setId("b689cd5d-2014-45e4-911f-f65197f70d44");
        record.setOwner(owner);
        record.setName("Citation, with only ISBN 020a");
        record.setDataFields(asList(new Datafield("020", 'a', "0786808772")));
        return  recordStore.save(record);
    }

    public Citation recordSurveyCatalogingPractices(User owner) {
        Datafield df1 = new Datafield("020", 'a', "1574403834");
        Datafield df2 = new Datafield("100", 'a', "Haider" + AUTHOR_NAME_ENCODING + "Salman");

        Datafield df3 = new Datafield("245", '1', '#',
                asList(
                        new Subfield('a', "Survey of emerging cataloging practices"),
                        new Subfield('b', "use of RDA by academic libraries")
                ));

        Datafield df4 = new Datafield("264", '1', '1',
                asList(
                        new Subfield('a', "New York"),
                        new Subfield('b', "Primary Research Group"),
                        new Subfield('c', "2016")
                ));


        Datafield df5 = new Datafield("300", '1', '1',
                asList(
                        new Subfield('a', "111 pages"),
                        new Subfield('b', "illustrations"),
                        new Subfield('c', "28 cm")
                ));

        Datafield df6 = new Datafield("710", 'a', "Primary Research Group");


        Citation record = new Citation();
        record.setId("ba1b339d-1825-47c2-8fdf-9b95c39ec159");
        record.setOwner(owner);
        record.setName("Průzkum archivace dát - kniha");
        record.setDataFields(asList(df1, df2, df3, df4, df5, df6));
        return  recordStore.save(record);

    }

    public Citation recordDanielSmith(User owner) {
        Datafield df1 = new Datafield("100", 'a', "Smith" + AUTHOR_NAME_ENCODING + "Daniel");

        Datafield df2 = new Datafield("245", '1', '0',
                asList(
                        new Subfield('a', "English music for bassoon and piano"),
                        new Subfield('h', "sound recording")
                ));

        Datafield df3 = new Datafield("300", '#', '1',
                asList(
                        new Subfield('a', "1 sound disc"),
                        new Subfield('b', "analog, 33 1/3 rpm, stereo"),
                        new Subfield('c', "12 in")
                ));

        Datafield df4 = new Datafield("700", 'a', "Vignoles" + AUTHOR_NAME_ENCODING + "Roger");
        Datafield df5 = new Datafield("700", 'a', "Hurlestone" + AUTHOR_NAME_ENCODING + "William Yeates");

        Datafield df6 = new Datafield("700", 'a', "Elgar" + AUTHOR_NAME_ENCODING + "Edward");

        Citation record = new Citation();
        record.setId("0cb44dd5-f339-4418-a9db-58d921045b17");
        record.setOwner(owner);
        record.setName("Zvuková nahrávka od Daniela Smitha");
        record.setDataFields(asList(df1, df2, df3, df4, df5, df6));
        return  recordStore.save(record);

    }

    public Citation recordAntiqueWorld(User owner) {
        Datafield df1 = new Datafield("100", 'a', "Harig" + AUTHOR_NAME_ENCODING + "Karl-F.");
        Datafield df2 = new Datafield("245", 'a', "ČTENÁŘŮV světový SVĚT starožitností");

        Datafield df3 = new Datafield("700", 'a', "Bocher" + AUTHOR_NAME_ENCODING + "Steen Bugge");
        Datafield df4 = new Datafield("700", 'a', "Hoffmeyer" + AUTHOR_NAME_ENCODING + "Henrik B.");
        Datafield df5 = new Datafield("710", 'a', "Reader's Digest Association");


        Citation record = new Citation();
        record.setId("193936ed-b941-4d7f-9831-ca89bc672646");
        record.setOwner(owner);
        record.setName("Mapa antického světa");
        record.setDataFields(asList(df1, df2, df3, df4, df5));
        return  recordStore.save(record);

    }

    public Citation recordWithHumanPrimaryAuthor(User owner) {
        Datafield f100 = new Datafield("100", 'a', "Doe" + AUTHOR_NAME_ENCODING + "John");

        Datafield f700_1 = new Datafield("700", 'a', "Marković" + AUTHOR_NAME_ENCODING + "Marko");
        Datafield f700_2 = new Datafield("700", 'a', "Zhang" + AUTHOR_NAME_ENCODING + "San");
        Datafield f700_3 = new Datafield("700", 'a', "Novák" + AUTHOR_NAME_ENCODING + "Josef");

        Datafield f710_1 = new Datafield("710", 'a', "Ghostronics");
        Datafield f710_2 = new Datafield("710", 'a', "Tuliproductions");
        Datafield f710_3 = new Datafield("710", 'a', "Javazo");

        Datafield f020 = new Datafield("020", 'a', "1574403834");
        Datafield f245 = new Datafield("245", 'a', "HUMAN Survey of emerging cataloging practices");

        Citation record = new Citation();
        record.setId("baaebbee-e96f-4f75-870c-693b2f49a181");
        record.setOwner(owner);
        record.setName("Primary Author: Human");
        record.setDataFields(asList(f020, f100, f245, f700_1, f700_2, f700_3, f710_1, f710_2, f710_3));
        return recordStore.save(record);

    }

    public Citation recordWithCompanyPrimaryAuthor(User owner) {
        Datafield f110 = new Datafield("110", 'a', "inQool");

        Datafield f700_1 = new Datafield("700", 'a', "Marković" + AUTHOR_NAME_ENCODING + "Marko");
        Datafield f700_2 = new Datafield("700", 'a', "Zhang" + AUTHOR_NAME_ENCODING + "San");
        Datafield f700_3 = new Datafield("700", 'a', "Novák" + AUTHOR_NAME_ENCODING + "Josef");

        Datafield f710_1 = new Datafield("710", 'a', "Ghostronics");
        Datafield f710_2 = new Datafield("710", 'a', "Tuliproductions");
        Datafield f710_3 = new Datafield("710", 'a', "Javazo");

        Datafield f020 = new Datafield("020", 'a', "1574403834");
        Datafield f245 = new Datafield("245", 'a', "COMPANY Survey of emerging cataloging practices");

        Citation record = new Citation();
        record.setId("92e0844d-4774-4d79-9260-41ed9a8003e3");
        record.setOwner(owner);
        record.setName("Primary Author: Company");
        record.setDataFields(asList(f020, f110, f245, f700_1, f700_2, f700_3, f710_1, f710_2, f710_3));


        return  recordStore.save(record);
    }

    public Citation briefRecord1(User owner) {
        Citation record = new Citation();
        record.setId("b357f25c-2a8c-48e8-b553-d55b00fbe761");
        record.setOwner(owner);
        record.setName("Rychla citace 01");
        record.setContent("Tohle je copyepaste test.");

        return recordStore.save(record);
    }

    @Override
    public void wipeAllDatabaseData() {
        recordStore.clearTable();
    }

}