///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////  Supporting Functions to Preprocess Text and doing some utilities  ///////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


package utilities;
import java.util.Set;

/**
 * Created by monadiab on 1/8/16.
 */
public class Preprocessing {

    public static String getSafeBW(String BW)
    {
        String tempSafeBW="";
        tempSafeBW=BW;

        if (tempSafeBW.contains("|"))
            tempSafeBW= tempSafeBW.replace("|","M");
        if(tempSafeBW.contains("<"))
            tempSafeBW=tempSafeBW.replace("<","I");
        if(tempSafeBW.contains(">"))
            tempSafeBW=tempSafeBW.replace(">","O");
        if(tempSafeBW.contains("&"))
            tempSafeBW=tempSafeBW.replace("&","W");
        if(tempSafeBW.contains("}"))
            tempSafeBW=tempSafeBW.replace("}","Q");
        if(tempSafeBW.contains("*"))
            tempSafeBW=tempSafeBW.replace("*","V");
        if(tempSafeBW.contains("$"))
            tempSafeBW=tempSafeBW.replace("$","c");
        if(tempSafeBW.contains("'"))
            tempSafeBW=tempSafeBW.replace("'","C");
        if(tempSafeBW.contains("{"))
            tempSafeBW=tempSafeBW.replace("{","L");
        if(tempSafeBW.contains("`"))
            tempSafeBW=tempSafeBW.replace("`","e");

        return tempSafeBW;
    }


    public static String normalizeAlefYa (String originalWord)
    {
        return originalWord.replace("|","A").replace("<","A").replace(">","A").replace("{","A").replace("`","A").replace("Y","y");
    }

    public static String normalizeAlefYa_in_SafeBW (String originalWord)
    {
        return originalWord.replace("M","A").replace("I","A").replace("O","A").replace("L","A").replace("e","A").replace("Y","y");
    }

    public static String undiacratize (String originalWord)
    {
        return originalWord.replaceAll("[FNKauio~]", "");

    }

    public static String removeInsideWordPlus(String str){
        String cleanedStr=str;
        boolean hasLeadingPlus=false;
        boolean hasTailingPlus=false;
        if (str.charAt(0)=='+')
        {
            hasLeadingPlus=true;
            cleanedStr=str.substring(1,str.length());
        }
        if(str.charAt(str.length()-1)=='+'){
            hasTailingPlus=true;
            cleanedStr=str.substring(0,str.length()-1);
        }
        if (cleanedStr.contains("+"))
            cleanedStr=cleanedStr.replace("+","");

        if (hasLeadingPlus==true)
            cleanedStr="+"+cleanedStr;
        if(hasTailingPlus==true)
            cleanedStr=cleanedStr+"+";

        return cleanedStr;
    }

    public static Set intersect(Set coll1, Set coll2) {
        Set intersection = coll1;
        intersection.retainAll(coll2);
        return intersection;
    }

    public static Set nonOverLap(Set coll1, Set coll2) {
        Set nonOverlap = coll1;
        nonOverlap.removeAll(intersect(coll1, coll2));
        return nonOverlap;
    }
}
