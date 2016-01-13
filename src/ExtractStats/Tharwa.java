package ExtractStats;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import utilities.*;

/**
 * Created by monadiab on 1/8/16.
 */
public class Tharwa {

    //tharwa information
    static ArrayList<Integer> tharwa_DIA = new ArrayList<Integer>();
    static ArrayList<String> tharwa_DIA_MSA= new ArrayList<String>();
    static ArrayList<String> tharwa_DIA_MSA_EN= new ArrayList<String>();
    static int tharwaSize=0;

    public static HashMap<Integer, Vector<MSAEnTuple>> ConvertTharwaToDic(String tharwaPath, boolean AlefYaNormalized, boolean diacritized) throws IOException
    {
        System.out.println("***************************");
        System.out.println("Convert ExtractStats.Tharwa To Dic Started...\n");

        File tharwaDir= new File(tharwaPath);
        String outputDir= tharwaDir.getParent();

        BufferedWriter tharwaWriter=new BufferedWriter(new FileWriter(outputDir+"/tharwa"));
        BufferedReader tharwaReader = new BufferedReader(new FileReader(tharwaPath));
        HashMap<Integer, Vector<MSAEnTuple>> tharwaDic=new HashMap<Integer, Vector<MSAEnTuple>>();

        String DIA_LEMMA = "";
        String MSA_Lemma = "";
        String CODA= "";
        String Eng_gloss="";
        Integer id= 0;
        String LineToRead= tharwaReader.readLine();
        while ((LineToRead =tharwaReader.readLine()) != null)
        {
            String[] splitedLine = LineToRead.split("\t");

            id= Integer.parseInt(splitedLine[0]);
            DIA_LEMMA = splitedLine[6]; // EGY_word column
            MSA_Lemma= splitedLine[4];  //MSA-Lemma column
            CODA= splitedLine[2];      //CODA column
            Eng_gloss= splitedLine[7]; // English-Equivalent column
            String [] temp_ENG_glosses= Eng_gloss.replace("##",";;").split(";;");
            Vector<String> ENG_glosses= new Vector<String>();
            for (String En_token:temp_ENG_glosses)
            {
                if (En_token.contains("/"))
                {
                    if (!En_token.contains(" "))
                    {
                        if (!ENG_glosses.contains(En_token.split("/")[0]))
                            ENG_glosses.add(En_token.split("/")[0]);
                        if (!ENG_glosses.contains(En_token.split("/")[1]))
                            ENG_glosses.add(En_token.split("/")[1]);
                    }
                    else
                    {
                        String[] spaceSeparatedTokens= En_token.split(" ");
                        String[] slashSeparatedToken_1= spaceSeparatedTokens[0].split("/");
                        String[] slashSeparatedToken_2= spaceSeparatedTokens[1].split("/");

                        for (int i=0;i< slashSeparatedToken_1.length;i++)
                        {
                            for (int j=0;j< slashSeparatedToken_2.length;j++)
                            {
                                if (!ENG_glosses.contains(slashSeparatedToken_1[i]+" "+slashSeparatedToken_2[j]))
                                    ENG_glosses.add(slashSeparatedToken_1[i]+" "+slashSeparatedToken_2[j]);
                            }
                        }
                    }
                }
                else
                {
                    if (!ENG_glosses.contains(En_token))
                        ENG_glosses.add(En_token);

                }
            }

            if (AlefYaNormalized==true)
            {
                if (diacritized==false)
                {
                    DIA_LEMMA = Preprocessing.getSafeBW(Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(DIA_LEMMA)));
                    MSA_Lemma = Preprocessing.getSafeBW(Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(MSA_Lemma)));
                    CODA = Preprocessing.getSafeBW(Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(CODA)));
                }
                else {
                    DIA_LEMMA = Preprocessing.getSafeBW(Preprocessing.normalizeAlefYa(DIA_LEMMA));
                    MSA_Lemma = Preprocessing.getSafeBW(Preprocessing.normalizeAlefYa(MSA_Lemma));
                    CODA = Preprocessing.getSafeBW(Preprocessing.normalizeAlefYa(CODA));
                }
            }
            else
            {
                if (diacritized== false)
                {
                    DIA_LEMMA= Preprocessing.getSafeBW(Preprocessing.undiacratize(DIA_LEMMA));
                    MSA_Lemma= Preprocessing.getSafeBW(Preprocessing.undiacratize(MSA_Lemma));
                    CODA= Preprocessing.getSafeBW(Preprocessing.undiacratize(CODA));
                }
                else
                {
                    DIA_LEMMA= Preprocessing.getSafeBW(DIA_LEMMA);
                    MSA_Lemma= Preprocessing.getSafeBW(MSA_Lemma);
                    CODA= Preprocessing.getSafeBW(CODA);
                }
            }
            // adding CODA to the CODA_freq dictionary separatly
            Indexing.add_CODA_freq_dic(CODA, id);

            // seprating Eng glosses: each entry might have different Eng equivalents seprated by ";"
            for (int i=0;i<ENG_glosses.size();i++)
            {
                if(!ENG_glosses.get(i).equals("UNK") && ENG_glosses.get(i).length()>0)
                {
                    // wrtining into a text file

                    // tharwaWriter.write(id.toString()+"\t"+DIA_LEMMA+'\t'+ MSA_Lemma+'\t'+ENG_gloss[i]+'\n');

                    // creating the thatwaDic using indexed dictionaries

                    Integer DIA_index=0;
                    Integer MSA_index = 0;
                    Integer En_index= 0;
                    //Using CODA word as DA word
                    if(!Indexing.DIA_indexDic.containsKey(CODA))
                        Indexing.update_DIA_Index_Dic(CODA);
                    if (!Indexing.MSA_indexDic.containsKey(MSA_Lemma))
                        Indexing.update_MSA_Index_Dic(MSA_Lemma);
                    if (! Indexing.En_indexDic.containsKey(ENG_glosses.get(i)))
                        Indexing.update_En_Index_Dic(ENG_glosses.get(i));

                    DIA_index = Indexing.get_DIA_Index(CODA);
                    MSA_index = Indexing.get_MSA_Index(MSA_Lemma);
                    En_index = Indexing.get_En_Index(ENG_glosses.get(i));
                    ArrayList<Integer> ids= new ArrayList<Integer>();
                    ids.add(id);

                    tharwaWriter.write(Indexing.get_En_Word(En_index)+'\t'+ Indexing.get_DIA_Word(DIA_index)+'\t'+Indexing.get_MSA_Word(MSA_index)+'\n');


                    MSAEnTuple touple= new MSAEnTuple(MSA_index,En_index,ids);

                    if (!tharwaDic.containsKey(DIA_index))
                    {
                        Vector<MSAEnTuple> tempVector= new Vector<MSAEnTuple>();
                        tempVector.add(touple);
                        tharwaDic.put(DIA_index, tempVector);
                        tharwaSize++;

                        tharwa_DIA.add(DIA_index);
                        tharwa_DIA_MSA.add(DIA_index+"\t"+MSA_index);
                        tharwa_DIA_MSA_EN.add(DIA_index+"\t"+MSA_index+"\t"+En_index);
                    }
                    else if (tharwaDic.containsKey(DIA_index))
                    {
                        Boolean isTheSame=false;
                        for (int k=0;k<tharwaDic.get(DIA_index).size();k++)
                        {
                            if (tharwaDic.get(DIA_index).elementAt(k).getMSA().equals(touple.getMSA()))
                            {
                                if (tharwaDic.get(DIA_index).elementAt(k).getEn().equals(touple.getEn()))
                                {
                                    tharwaDic.get(DIA_index).elementAt(k).setIds(id);
                                    isTheSame=true;
                                    break;
                                }

                            }
                            else
                                tharwa_DIA_MSA.add(DIA_index+"\t"+MSA_index);

                        }
                        if (isTheSame.equals(false))
                        {
                            tharwaDic.get(DIA_index).add(touple);
                            tharwaSize++;
                            tharwa_DIA_MSA_EN.add(DIA_index+"\t"+MSA_index+"\t"+En_index);
                        }
                    }

                }
            }


            if(id %10000==0)
                System.out.println(id);
        }
        tharwaWriter.flush();
        tharwaWriter.close();
        tharwaReader.close();

        System.out.println("ConvertTharwaToDic finished");
        System.out.println("Numbe of ExtractStats.Tharwa Entries "+ tharwaDic.size());
        System.out.println("***************************");
        return tharwaDic;
    }
}
