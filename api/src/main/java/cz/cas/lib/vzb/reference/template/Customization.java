package cz.cas.lib.vzb.reference.template;

/**
 * Enum representing text customization
 * BOLD, ITALIC, UPPERCASE are regular font styles.
 *
 * CONCAT_COMMA, CONCAT_SPACE represents a way to display data if there are multiple entries for same tag,code combination.
 *
 * 650  1 $a Fantasy.
 * 650  1 $a Baseball / $vFiction.
 * 650  1 $a Magic/ $v Fiction.
 *
 * In example above, there are multiple entries for combination of tag `650` and code `a`.
 * If user does not user CONCAT_COMMA then first retrieved data (first retrieved tag 650) from DB will be filled into template.
 * If user uses CONCAT_COMMA then this multiple entries will be concatenated.
 *
 * Eg. pattern: "${?} - Test"
 * Where placeholder `${?}` represents
 * {
 *    "tag":"650",
 *    "code":"a"
 * }
 * Without CONCAT_COMMA will this pattern be filled as one of:
 *  "Fantasy - Test" / "Baseball - Test" / "Magic - Test"
 *
 * With CONCAT_COMMA wil this pattern be filled as:
 *  "Fantasy, Baseball, Magic - Test"
 *
 * CONCAT_SPACE has same logic, but instead of joining data with comma, they are joined with space.
 * Please note that order of retrieved tags may wary depending on retrieval from DB)
 * TODO: Make it consistent, depending on client's wish of order by clause (Should it be date of insert/ alphabetical .. ? )
 * TODO: This sorting should be made during deserialization from DB
 *
 */
public enum Customization {
    BOLD, ITALIC, UPPERCASE, CONCAT_COMMA, CONCAT_SPACE
}
