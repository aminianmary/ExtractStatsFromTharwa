package ExtractStats;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import utilities.Indexing;
import utilities.Preprocessing;

/**
 * Created by Maryam Aminian
 */

//////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////  Building Alignment Dictionaries /////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////


public class ParallelCorpora {

    public static Object[] buildDA_EnAlignDic(String Ar_corpus, String En_corpus,
                                              String Allign_file,boolean AlefYaNormalized, boolean diacritized) throws IOException
    {

        System.out.println("Building DIA-En Alignment Dics Started...");

        BufferedReader Ar_data_reader = new BufferedReader(new FileReader(Ar_corpus));
        BufferedReader En_data_reader = new BufferedReader(new FileReader(En_corpus));
        BufferedReader Allign_file_reader = new BufferedReader(new FileReader(Allign_file));


        String Ar_line_to_read = "";
        String En_line_to_read = "";
        String Allign_line_to_read = "";

        HashMap<Integer, HashMap<Integer, Integer>>  En_Ar_Align_Dic = new  HashMap<Integer, HashMap<Integer, Integer>>();
        HashMap<Integer, HashMap<Integer, Integer>>  Ar_En_Align_Dic = new  HashMap<Integer, HashMap<Integer, Integer>>();

        int sentenceID = -1;
        int totalNumberTokens=0;
        int totalNumberEn_ArTuples=0;
        int totalNumberAr_EnTuples=0;

        while (((Ar_line_to_read = Ar_data_reader.readLine()) != null))
        {
            En_line_to_read = En_data_reader.readLine();
            Allign_line_to_read = Allign_file_reader.readLine();


            sentenceID++;
            //System.out.print(sentenceID + "\n");
            // System.out.print(Ar_line_to_read);
            //  System.out.print(En_line_to_read);
            // System.out.print(Allign_line_to_read);
            // System.out.println(Allign_line_to_read);
            if(sentenceID %10000==0)
                System.out.println(sentenceID);
            if (!Allign_line_to_read.trim().equals("") && !Ar_line_to_read.equals("") && !En_line_to_read.equals(""))
            {

                String[] Ar_words = Ar_line_to_read.split(" ");
                String[] En_words = En_line_to_read.split(" ");
                String[] Allign_words = Allign_line_to_read.split(" ");
                totalNumberTokens += Ar_words.length;

                for (int i = 0; i < Allign_words.length; i++) {
                    // finding indexes from alignment data
                    Integer ArabicIndex = Integer.parseInt(Allign_words[i].split("-")[0]);
                    Integer EngIndex = Integer.parseInt(Allign_words[i].toString().split("-")[1]);

                    // finding words
                    String ArabicWord = Preprocessing.removeInsideWordPlus(Ar_words[ArabicIndex]);
                    String ArabicLemma = ArabicWord;
                    String ArabicPOS = "";
                    //if word has POS
                    if (ArabicWord.contains("|")) {
                        ArabicLemma = ArabicWord.split("\\|")[0].trim();
                        ArabicPOS = ArabicWord.split("\\|")[1].trim();
                    }


                    String EngLemma = En_words[EngIndex];

                    if (AlefYaNormalized==true)
                    {
                        if (diacritized==false)
                        {
                            ArabicLemma = Preprocessing.getSafeBW(Preprocessing.normalizeAlefYa_in_SafeBW(Preprocessing.undiacratize(ArabicLemma)));
                        }
                        else {
                            ArabicLemma = Preprocessing.getSafeBW(Preprocessing.normalizeAlefYa_in_SafeBW(ArabicLemma));
                        }
                    }
                    else
                    {
                        if (diacritized== false)
                        {
                            ArabicLemma= Preprocessing.getSafeBW(Preprocessing.undiacratize(ArabicLemma));
                        }
                        else
                        {
                            ArabicLemma= Preprocessing.getSafeBW(ArabicLemma);
                        }
                    }


                    // finging indexes in indexed dictionaries for DIA data

                    Integer DIA_index = 0;
                    Integer En_index = 0;
                    Integer POS_index = 0;


                    if (!Indexing.DIA_indexDic.containsKey(ArabicLemma))
                        Indexing.update_DIA_Index_Dic(ArabicLemma);
                    if (!Indexing.En_indexDic.containsKey(EngLemma))
                        Indexing.update_En_Index_Dic(EngLemma);
                    if (!ArabicPOS.equals("") && !Indexing.POS_indexDic.containsKey(ArabicPOS))
                        Indexing.update_POS_Index_Dic(ArabicPOS);

                    DIA_index = Indexing.get_DIA_Index(ArabicLemma);
                    En_index = Indexing.get_En_Index(EngLemma);
                    POS_index = Indexing.get_POS_Index(ArabicPOS);

                    //System.out.println("Arabic Word: "+ ArabicWord);
                    //System.out.println("Arabic POS: "+ ArabicPOS);
                    //System.out.println("Retrived POS index: " + POS_index);


                    //Update En_Ar_Align_Dic
                    if (!En_Ar_Align_Dic.containsKey(En_index)) {
                        HashMap<Integer, Integer> tempHashMap = new HashMap<Integer, Integer>();
                        tempHashMap.put(DIA_index, POS_index);
                        En_Ar_Align_Dic.put(En_index, tempHashMap);
                        totalNumberEn_ArTuples += 1;
                    } else {
                        if (!En_Ar_Align_Dic.get(En_index).containsKey(DIA_index)) {
                            En_Ar_Align_Dic.get(En_index).put(DIA_index, POS_index);
                            totalNumberEn_ArTuples += 1;
                        } else if (En_Ar_Align_Dic.get(En_index).get(DIA_index) != POS_index) {
                            En_Ar_Align_Dic.get(En_index).put(DIA_index, POS_index);
                            totalNumberEn_ArTuples += 1;
                        }
                    }

                    //Update Ar_En_Align_Dic
                    if (!Ar_En_Align_Dic.containsKey(DIA_index)) {
                        HashMap<Integer, Integer> tempHashMap = new HashMap<Integer, Integer>();
                        tempHashMap.put(En_index, POS_index);
                        Ar_En_Align_Dic.put(DIA_index, tempHashMap);
                        totalNumberAr_EnTuples += 1;
                    } else {
                        if (!Ar_En_Align_Dic.get(DIA_index).containsKey(En_index)) {
                            Ar_En_Align_Dic.get(DIA_index).put(En_index, POS_index);
                            totalNumberAr_EnTuples += 1;
                        } else if (Ar_En_Align_Dic.get(DIA_index).get(En_index) != POS_index) {
                            Ar_En_Align_Dic.get(DIA_index).put(En_index, POS_index);
                            totalNumberAr_EnTuples += 1;
                        }
                    }
                }
            }
            else
            {
                if (Allign_line_to_read.equals(""))
                    System.out.println("Sentence "+ sentenceID+": alignment is empty");

                if (En_line_to_read.equals(""))
                    System.out.println("Sentence "+ sentenceID+": English is empty");

                if (Ar_line_to_read.equals(""))
                    System.out.println("Sentence "+ sentenceID+": Arabic is empty");
            }
        }

        Ar_data_reader.close();
        En_data_reader.close();
        Allign_file_reader.close();

        System.out.println("Building DA-En Alignment Dics Finished!");
        System.out.println("En_Index_dic size " + Indexing.En_indexDic.size());
        System.out.println("DA_Index_Dic_dic size " + Indexing.DIA_indexDic.size()+"\n----------------\n");

        System.out.println("Total Number of Tokens in the Arabic side  " + totalNumberTokens);
        System.out.println("Total Number of Tuples in the En-Ar Alignment Dic  "+totalNumberEn_ArTuples);
        System.out.println("Total Number of Tuples in the Ar-En Alignment Dic  "+totalNumberAr_EnTuples+"\n" +
                "----------------\n");

        System.out.println("Total Number of Entries in DA-En Alignment Dic " + En_Ar_Align_Dic.size());
        System.out.println("Total Number of Entries in En-DA Alignment Dic " + Ar_En_Align_Dic.size());

        System.out.println("***************************\n");

        for (String pos:Indexing.POS_indexDic.keySet())
        {
            System.out.println("POS: "+pos+" index: "+Indexing.POS_indexDic.get(pos));
        }

        return  new Object[]{En_Ar_Align_Dic, Ar_En_Align_Dic};

    }


    public static Object[] buildMSA_EnAllignDataDic(String Ar_corpus, String En_corpus,
                                                    String Allign_file, boolean AlefYaNormalized, boolean diacritized) throws IOException
    {

        System.out.println("Building MSA-En Alignment Dic Started...");

        BufferedReader Ar_data_reader = new BufferedReader(new FileReader(Ar_corpus));
        BufferedReader En_data_reader = new BufferedReader(new FileReader(En_corpus));
        BufferedReader Allign_file_reader = new BufferedReader(new FileReader(Allign_file));

        String Ar_line_to_read = "";
        String En_line_to_read = "";
        String Allign_line_to_read = "";

        HashMap<Integer, HashMap<Integer, Integer>>  En_Ar_Align_Dic = new  HashMap<Integer, HashMap<Integer, Integer>>();
        HashMap<Integer, HashMap<Integer, Integer>> Ar_En_Align_Dic = new  HashMap<Integer, HashMap<Integer, Integer>>();

        int totalNumberEn_ArTuples=0;
        int totalNumberAr_EnTuples=0;

        int sentenceID = -1;
        int totalNumberTokens=0;

        while (((Ar_line_to_read = Ar_data_reader.readLine()) != null))
        {
            En_line_to_read = En_data_reader.readLine();
            Allign_line_to_read = Allign_file_reader.readLine();

            sentenceID++;
            if (sentenceID%100000==0)
                System.out.print(sentenceID + "\n");
            //System.out.print(Ar_line_to_read);
            // System.out.print(En_line_to_read);
            //System.out.print(Allign_line_to_read);
            //System.out.println(Allign_line_to_read);
            if (!Allign_line_to_read.trim().equals("") && !Ar_line_to_read.equals("") && !En_line_to_read.equals(""))
            {
                String[] Ar_words = Ar_line_to_read.split(" ");
                String[] En_words = En_line_to_read.split(" ");
                String[] Allign_words = Allign_line_to_read.split(" ");
                totalNumberTokens += Ar_words.length;

                for (int i = 0; i < Allign_words.length; i++) {
                    // finding indexes from alignment data
                    Integer ArabicIndex = Integer.parseInt(Allign_words[i].split("-")[0]);
                    Integer EngIndex = Integer.parseInt(Allign_words[i].toString().split("-")[1]);

                    // finding words
                    String ArabicWord = Preprocessing.removeInsideWordPlus(Ar_words[ArabicIndex]);
                    String ArabicLemma = ArabicWord;
                    String ArabicPOS = "";


                    //if word has POS tag
                    if (ArabicWord.contains("|")) {
                        ArabicLemma = ArabicWord.split("\\|")[0].trim();
                        ArabicPOS = ArabicWord.split("\\|")[1].trim();
                    }


                    String EngLemma = En_words[EngIndex];

                    if (AlefYaNormalized==true)
                    {
                        if (diacritized==false)
                        {
                            ArabicLemma = Preprocessing.getSafeBW(
                                    Preprocessing.normalizeAlefYa_in_SafeBW(Preprocessing.undiacratize(ArabicLemma)));
                        }
                        else {
                            ArabicLemma = Preprocessing.getSafeBW(Preprocessing.normalizeAlefYa_in_SafeBW(ArabicLemma));
                        }
                    }
                    else
                    {
                        if (diacritized== false)
                        {
                            ArabicLemma= Preprocessing.getSafeBW(Preprocessing.undiacratize(ArabicLemma));
                        }
                        else
                        {
                            ArabicLemma= Preprocessing.getSafeBW(ArabicLemma);
                        }
                    }

                    // finging indexes in indexed dictionaries for DIA data

                    Integer MSA_index = 0;
                    Integer En_index = 0;
                    Integer POS_index = 0;

                    if (!Indexing.MSA_indexDic.containsKey(ArabicLemma))
                        Indexing.update_MSA_Index_Dic(ArabicLemma);
                    if (!Indexing.En_indexDic.containsKey(EngLemma))
                        Indexing.update_En_Index_Dic(EngLemma);
                    if (!ArabicPOS.equals("") && !Indexing.POS_indexDic.containsKey(ArabicPOS))
                        Indexing.update_POS_Index_Dic(ArabicPOS);


                    MSA_index = Indexing.get_MSA_Index(ArabicLemma);
                    En_index = Indexing.get_En_Index(EngLemma);
                    POS_index = Indexing.get_POS_Index(ArabicPOS);

                    //System.out.println("Arabic Word: "+ ArabicWord);
                    //System.out.println("Arabic POS: "+ ArabicPOS);
                    //System.out.println("Retrived POS index: " + POS_index);

                    // Constructing En_Ar_Alignment Dictionary
                    if (!En_Ar_Align_Dic.containsKey(En_index)) {
                        HashMap<Integer, Integer> tempHashMap = new HashMap<Integer, Integer>();

                        tempHashMap.put(MSA_index, POS_index);

                        En_Ar_Align_Dic.put(En_index, tempHashMap);
                        totalNumberEn_ArTuples += 1;
                    } else {
                        if (!En_Ar_Align_Dic.get(En_index).containsKey(MSA_index)) {
                            En_Ar_Align_Dic.get(En_index).put(MSA_index, POS_index);
                            totalNumberEn_ArTuples += 1;
                        } else if (En_Ar_Align_Dic.get(En_index).get(MSA_index) != POS_index) {
                            En_Ar_Align_Dic.get(En_index).put(MSA_index, POS_index);
                            totalNumberEn_ArTuples += 1;
                        }
                    }

                    //Update Ar_En_Align_Dic
                    if (!Ar_En_Align_Dic.containsKey(MSA_index)) {
                        HashMap<Integer, Integer> tempHashMap = new HashMap<Integer, Integer>();
                        tempHashMap.put(En_index, POS_index);

                        Ar_En_Align_Dic.put(MSA_index, tempHashMap);

                        totalNumberAr_EnTuples += 1;
                    } else {
                        if (!Ar_En_Align_Dic.get(MSA_index).containsKey(En_index)) {
                            Ar_En_Align_Dic.get(MSA_index).put(En_index, POS_index);
                            totalNumberAr_EnTuples += 1;
                        } else if (Ar_En_Align_Dic.get(MSA_index).get(En_index) != POS_index) {
                            Ar_En_Align_Dic.get(MSA_index).put(En_index, POS_index);
                            totalNumberAr_EnTuples += 1;
                        }

                    }
                }
            }
            else
            {
                if (Allign_line_to_read.equals(""))
                    System.out.println("Sentence "+ sentenceID+": alignment is empty");

                if (En_line_to_read.equals(""))
                    System.out.println("Sentence "+ sentenceID+": English is empty");

                if (Ar_line_to_read.equals(""))
                    System.out.println("Sentence "+ sentenceID+": Arabic is empty");
            }

        }

        Ar_data_reader.close();
        En_data_reader.close();
        Allign_file_reader.close();

        System.out.println("Building MSA-En Alignment Dics Finished!");
        System.out.println("En_Index_dic size " + Indexing.En_indexDic.size());
        System.out.println("MSA_Index_Dic_dic size " + Indexing.MSA_indexDic.size()+"\n----------------\n");

        System.out.println("Total Number of Tokens in the Arabic side  " + totalNumberTokens);
        System.out.println("Total Number of Tuples in the En-Ar Alignment Dic  "+totalNumberEn_ArTuples);
        System.out.println("Total Number of Tuples in the Ar-En Alignment Dic  "+totalNumberAr_EnTuples+"\n" +
                "----------------\n");

        System.out.println("Total Number of Entries in MSA-En Alignment Dic " + En_Ar_Align_Dic.size());
        System.out.println("Total Number of Entries in En-MSA Alignment Dic " + Ar_En_Align_Dic.size());

        System.out.println("***************************\n");

        return new Object[]{En_Ar_Align_Dic, Ar_En_Align_Dic};

    }

}
