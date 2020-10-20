package utils.helpers

import play.api.libs.json.{JsBoolean, JsLookupResult, JsObject, JsString, Json}

import scala.collection.mutable
import scala.util.matching.Regex

object RuleParser {

  def parseIndividualRules(rule: String, fieldMappings: mutable.HashMap[String, String]): Map[String, String] = {
    val jsonRule = Json.parse(rule)
    val individualRules = mutable.MutableList.empty[(String, String)]
    val sponsorId = (jsonRule \ ("sponsorId")).as[JsString].value
    val startDate = (jsonRule \ ("startDate")).as[JsString].value.substring(0, 10)
    val endDate = if (jsonRule.as[JsObject].keys.contains("endDate")) ((jsonRule) \ ("endDate")).as[JsString].value.substring(0, 10) else "9999-12-31"

    val sponsorIdRule = s"(CustomerId='${sponsorId}')"
    val dateRule = s"((E.EligibilityStartDate >= '${startDate}' AND E.EligibilityStartDate <= '${endDate}') OR (E.EligibilityEndDate >= '${startDate}' AND E.EligibilityEndDate <= '${endDate}') OR (E.EligibilityStartDate <= '${startDate}' AND E.EligibilityEndDate >= '${endDate}'))"
    handleI((jsonRule \ ("rules") \ ("hierarchical")).as[JsObject], fieldMappings, individualRules)
    handleI((jsonRule \ ("rules") \ ("standard")).as[JsObject], fieldMappings, individualRules)

    (for (individualRule <- individualRules) yield (individualRule._1,s"${sponsorIdRule} AND ${dateRule} AND ${individualRule._2}")).toMap
  }

  def parseIndividualRule(rule: String, fieldMappings: mutable.HashMap[String, String]): (String, String,Boolean) = {
    val jsonRule = Json.parse(rule)
    val sponsorId = (jsonRule \ ("sponsorId")).as[JsString].value
    val startDate = (jsonRule \ ("startDate")).as[JsString].value.substring(0, 10)
    val endDate = if (jsonRule.as[JsObject].keys.contains("endDate")) ((jsonRule) \ ("endDate")).as[JsString].value.substring(0, 10) else "9999-12-31"

    val sponsorIdRule = s"(CustomerId='${sponsorId}')"
    val dateRule = s"((E.EligibilityStartDate >= '${startDate}' AND E.EligibilityStartDate <= '${endDate}') OR (E.EligibilityEndDate >= '${startDate}' AND E.EligibilityEndDate <= '${endDate}') OR (E.EligibilityStartDate <= '${startDate}' AND E.EligibilityEndDate >= '${endDate}'))"
    val individualRules=handleSingleRule((jsonRule \ ("rule")).as[JsObject], fieldMappings, null)
    //println(s"abc ${individualRules(0)}")
    var sqlRule=""
    if(individualRules._2!=""){
      sqlRule= s"AND ${individualRules._2}"
    }
    (individualRules._1,s"${sponsorIdRule} AND ${dateRule} ${sqlRule}",individualRules._3)
  }



  def handleI(entry: JsObject, fieldMappings: mutable.HashMap[String, String], uuidList: mutable.MutableList[(String, String)]): List[(String, String)] = {

    if ((entry\\("key")).length == 0) {
      List.empty[(String, String)]
    } else {

      if (entry.keys.contains("and")) {

        andOrI((entry \ ("and")).as[List[JsObject]], fieldMappings, uuidList)

      }
      else if (entry.keys.contains("or")) {

        andOrI((entry \ ("or")).as[List[JsObject]], fieldMappings, uuidList)

      } else {
        if ((entry \\ ("uuid")).length > 0) {
          val key = (new Regex("^Misc[1-9][0-9]?$|^Misc100$")).findFirstIn((entry \ ("key")).as[JsString].value).getOrElse(fieldMappings.apply((entry \ ("key")).as[JsString].value))
          val value = (entry \ ("value")).as[JsString].value
          val `type` = if (entry.keys.contains("type")) (entry \ ("type")).as[JsString].value else ""
          val delimiter = if (entry.keys.contains("delimiter")) (entry \ ("delimiter")).as[JsString].value else ""
          val include = (entry \ ("include")).as[JsBoolean].value
          val operator = if (entry.keys.contains("operator")) (entry \ ("operator")).as[JsString].value else "="
          val quote = if (entry.keys.contains("operator")) "" else "'"
          val includeStr = if (include) "" else "NOT"
          val uuid = (entry \ ("uuid")).as[JsString].value

          // In the case of a delimited field check using the delimiter if provided, if not, check using both ; and , as delimiters.

          val appendCondition = if (`type` == "delimited") {
            if (delimiter != "")
              s"((${key} = ${quote}${value}${quote}) OR (${key} LIKE ${quote}%${delimiter}${value}${delimiter}%${quote}) OR (${key} LIKE ${quote}%${delimiter}${value}${quote}) OR (${key} LIKE ${quote}${value}${delimiter}%${quote}))"
            else
              s"((${key} = ${quote}${value}${quote}) OR (${key} LIKE ${quote}%;${value};%${quote}) OR (${key} LIKE ${quote}%;${value}${quote}) OR (${key} LIKE ${quote}${value};%${quote}) OR (${key} LIKE ${quote}%,${value},%${quote}) OR (${key} LIKE ${quote}%,${value}${quote}) OR (${key} LIKE ${quote}${value},%${quote}))"
          }else {
            s"(${key} ${operator} ${quote}${value}${quote})"
          }

          List((uuid, appendCondition))
        } else {
          List.empty[(String, String)]
        }

      }
    }
  }

  def handleSingleRule(entry: JsObject, fieldMappings: mutable.HashMap[String, String], uuidList: mutable.MutableList[(String, String)]): (String, String, Boolean) = {

    if (((entry\\("key")).length == 0 || (entry \\ ("uuid")).length == 0) || ((entry.keys.contains("and")) || (entry.keys.contains("or")))) {
      ("", "",false)
    } else {
      val key=if((entry \ ("key")).as[JsString].value=="county_fips"){ (entry \ ("key")).as[JsString].value} else
        (new Regex("^Misc[1-9][0-9]?$|^Misc100$")).findFirstIn((entry \ ("key")).as[JsString].value).getOrElse(fieldMappings.apply((entry \ ("key")).as[JsString].value))
      //val key = (new Regex("^Misc[1-9][0-9]?$|^Misc100$")).findFirstIn((entry \ ("key")).as[JsString].value).getOrElse(keyName)
      val value = (entry \ ("value")).as[JsString].value
      val `type` = if (entry.keys.contains("type")) (entry \ ("type")).as[JsString].value else ""
      val delimiter = if (entry.keys.contains("delimiter")) (entry \ ("delimiter")).as[JsString].value else ""
      //val include = (entry \ ("include")).as[JsBoolean].value
      val operator = if (entry.keys.contains("operator")) (entry \ ("operator")).as[JsString].value else "="
      val quote = if (entry.keys.contains("operator")) "" else "'"
      //val includeStr = if (include) "" else "NOT"
      val uuid = (entry \ ("uuid")).as[JsString].value

      // In the case of a delimited field check using the delimiter if provided, if not, check using both ; and , as delimiters.

      if (`type` == "delimited") {
        if (delimiter != "")
          (uuid,s"((${key} = ${quote}${value}${quote}) OR (${key} LIKE ${quote}%${delimiter}${value}${delimiter}%${quote}) OR (${key} LIKE ${quote}%${delimiter}${value}${quote}) OR (${key} LIKE ${quote}${value}${delimiter}%${quote}))",false)
        else
          (uuid,s"((${key} = ${quote}${value}${quote}) OR (${key} LIKE ${quote}%;${value};%${quote}) OR (${key} LIKE ${quote}%;${value}${quote}) OR (${key} LIKE ${quote}${value};%${quote}) OR (${key} LIKE ${quote}%,${value},%${quote}) OR (${key} LIKE ${quote}%,${value}${quote}) OR (${key} LIKE ${quote}${value},%${quote}))",false)
      }else if(key=="county_fips") {
        (uuid,s"(${key} ${operator} ${quote}${value}${quote})",true)
      }else {
        (uuid,s"(${key} ${operator} ${quote}${value}${quote})",false)
      }

    }

  }

  private def andOrI(entries: List[JsObject], fieldMappings: mutable.HashMap[String, String], uuidList: mutable.MutableList[(String, String)]): List[(String, String)] = {
    for (entry <- entries) {
      val deltaList = handleI(entry, fieldMappings, uuidList)
      uuidList ++= (deltaList diff uuidList)
    }
    uuidList.toList
  }


  def parseRule(rule: String, fieldMappings: mutable.HashMap[String, String]): String = {
    val jsonRule = Json.parse(rule)

    val sponsorId = (jsonRule\("sponsorId")).as[JsString].value
    val startDate = (jsonRule\("startDate")).as[JsString].value.substring(0,10)
    val endDate = if (jsonRule.as[JsObject].keys.contains("endDate")) ((jsonRule)\("endDate")).as[JsString].value.substring(0,10) else "9999-12-31"

    val sponsorIdRule = s"(CustomerId='${sponsorId}')"
    val dateRule = s"((E.EligibilityStartDate >= '${startDate}' AND E.EligibilityStartDate <= '${endDate}') OR (E.EligibilityEndDate >= '${startDate}' AND E.EligibilityEndDate <= '${endDate}') OR (E.EligibilityStartDate <= '${startDate}' AND E.EligibilityEndDate >= '${endDate}'))"
    val hRule = if (((jsonRule\("rules")).as[JsObject]).keys.contains("hierarchical")) handle((jsonRule\("rules")\("hierarchical")).as[JsObject], fieldMappings) else "(1=1)"
    val sRule = if (((jsonRule\("rules")).as[JsObject]).keys.contains("standard")) handle((jsonRule\("rules")\("standard")).as[JsObject], fieldMappings) else "(1=1)"

    s"(${sponsorIdRule} AND ${dateRule} AND ${hRule} AND ${sRule})"
  }

  def parseRuleJson(jsonRule: JsObject, fieldMappings: mutable.HashMap[String, String]): String = {
    //val jsonRule = Json.parse(rule)

    val sponsorId = (jsonRule\("sponsorId")).as[JsString].value
    val startDate = (jsonRule\("startDate")).as[JsString].value.substring(0,10)
    val endDate = if (jsonRule.as[JsObject].keys.contains("endDate")) ((jsonRule)\("endDate")).as[JsString].value.substring(0,10) else "9999-12-31"

    val sponsorIdRule = s"(CustomerId='${sponsorId}')"
    val dateRule = s"((E.EligibilityStartDate >= '${startDate}' AND E.EligibilityStartDate <= '${endDate}') OR (E.EligibilityEndDate >= '${startDate}' AND E.EligibilityEndDate <= '${endDate}') OR (E.EligibilityStartDate <= '${startDate}' AND E.EligibilityEndDate >= '${endDate}'))"
    val hRule = if (((jsonRule\("rules")).as[JsObject]).keys.contains("hierarchical")) handle((jsonRule\("rules")\("hierarchical")).as[JsObject], fieldMappings) else "(1=1)"
    val sRule = if (((jsonRule\("rules")).as[JsObject]).keys.contains("standard")) handle((jsonRule\("rules")\("standard")).as[JsObject], fieldMappings) else "(1=1)"

    s"(${sponsorIdRule} AND ${dateRule} AND ${hRule} AND ${sRule})"
  }

  def handle(entry: JsObject, fieldMappings: mutable.HashMap[String, String]): String = {

    if ((entry\\("key")).length == 0) {
      "1=1"
    } else {

      if (entry.keys.contains("and")) {

        val andClause = and((entry \ ("and")).as[List[JsObject]], fieldMappings)
        s"(${andClause})"

      }
      else if (entry.keys.contains("or")) {

        val orClause = or((entry \ ("or")).as[List[JsObject]], fieldMappings)
        s"(${orClause})"

      } else {

        // If Misc field use the field directly (no need for explicit mappings)
        val key = (new Regex("^Misc[1-9][0-9]?$|^Misc100$")).findFirstIn((entry \ ("key")).as[JsString].value).getOrElse(fieldMappings.apply((entry \ ("key")).as[JsString].value))
        val value = (entry \ ("value")).as[JsString].value
        val `type` = if (entry.keys.contains("type")) (entry \ ("type")).as[JsString].value else ""
        val delimiter = if (entry.keys.contains("delimiter")) (entry \ ("delimiter")).as[JsString].value else ""
        val include = (entry \ ("include")).as[JsBoolean].value
        val operator = if (entry.keys.contains("operator")) (entry \ ("operator")).as[JsString].value else "="
        val quote = if (entry.keys.contains("operator")) "" else "'"
        val includeStr = if (include) "" else "NOT"

        // In the case of a delimited field check using the delimiter if provided, if not, check using both ; and , as delimiters.

        val appendCondition = if (`type` == "delimited") {
          if (delimiter != "")
            s"((${key} = ${quote}${value}${quote}) OR (${key} LIKE ${quote}%${delimiter}${value}${delimiter}%${quote}) OR (${key} LIKE ${quote}%${delimiter}${value}${quote}) OR (${key} LIKE ${quote}${value}${delimiter}%${quote}))"
          else
            s"((${key} = ${quote}${value}${quote}) OR (${key} LIKE ${quote}%;${value};%${quote}) OR (${key} LIKE ${quote}%;${value}${quote}) OR (${key} LIKE ${quote}${value};%${quote}) OR (${key} LIKE ${quote}%,${value},%${quote}) OR (${key} LIKE ${quote}%,${value}${quote}) OR (${key} LIKE ${quote}${value},%${quote}))"
        } else s"(${key} ${operator} ${quote}${value}${quote})"


        s"${includeStr} ${appendCondition}"

      }
    }
  }

  private def and(entries: List[JsObject], fieldMappings: mutable.HashMap[String, String]): String = {
    (for (entry <- entries) yield handle(entry, fieldMappings)).mkString(" AND ")
  }

  private def or(entries: List[JsObject], fieldMappings: mutable.HashMap[String, String]): String = {
    (for (entry <- entries) yield handle(entry, fieldMappings)).mkString(" OR ")
  }

  def parseMembershipRuleMap(rule: String, fieldMappings: mutable.HashMap[String, String]): List[(String, String)] = {
    val jsonRule = Json.parse(rule)

    val memberAttributes = (jsonRule\("demographics")).as[Map[String, String]]

    ((for ((k,v) <- memberAttributes) yield (fieldMappings.apply(k), (if (k=="gender") {(if (v=="M") "02" else "03")} else v))).toList)

  }

}
