package ExtractStats;
/**
 * User: Maryam Aminian
 * Date: 1/24/14
 * Time: 10:41 PM
 * This java class is created to extract matching statistics between Tharwa and existing Parallel corpora
 */

import utilities.Indexing;
import utilities.MSAEnTuple;

import java.io.*;
import java.util.*;

public class ExtractStats {

    static HashMap<Integer,HashMap<Integer,ArrayList<Object>>>  DA_sim_En_diff_MSA_sim_w_POS_found_rows= new HashMap<Integer,HashMap<Integer,ArrayList<Object>>>();
    static HashMap<Integer,HashMap<Integer,ArrayList<Set<Integer>>>>  DA_sim_En_diff_MSA_sim_found_rows= new HashMap<Integer,HashMap<Integer,ArrayList<Set<Integer>>>>();

   //Stats
   static int DA_sim_En_sim_MSA_sim=0;
   static int DA_sim_En_sim_MSA_diff=0;
   static int DA_sim_En_diff_MSA_sim=0;
   static int DA_sim_En_diff_MSA_diff=0;

    static int DA_diff_En_sim_MSA_sim=0;
    static int DA_diff_En_sim_MSA_diff=0;
    static int DA_diff_En_diff_MSA_sim=0;
    //static int DA_diff_En_diff_MSA_diff=0;

    static int DA_sim_En_sim_MSA_sim_w_POS=0;
    static int DA_sim_En_sim_MSA_diff_w_POS=0;
    static int DA_sim_En_diff_MSA_sim_w_POS=0;
    static int DA_sim_En_diff_MSA_diff_w_POS=0;

    static int DA_diff_En_sim_MSA_sim_w_POS=0;
    static int DA_diff_En_sim_MSA_diff_w_POS=0;
    static int DA_diff_En_diff_MSA_sim_w_POS=0;
    //static int DA_diff_En_diff_MSA_diff_w_POS=0;

    static int transDicEntries_Dropped_Due_to_diff_POS=0;



   public static void main(String[] args) throws IOException
    {
        /*
        args[0] = DIA_En_Alignment_Path
        args[1] = MSA_En_Alignment_Path
        args[2] = tharwaPath
        args[3] = DIA_En_EngDataPath
        args[4] = DIA_En_ArDataPath
        args[5] = MSA_En_EngDataPath
        args[6] = MSA_En_ArDataPath
        args[7] = Alef-Ya normalized
        args[8] = lemmatized (boolean)
        args[9] = diacritized (boolean)
        args[10]= Include POS in making triplets (boolean)
        args[11]= output Directory
         */

        if (args.length < 11)
            System.out.println("MISSED ARGUMENT");

        String DIA_En_Alignment_Path = args [0];
        String MSA_En_Alignment_Path =args [1];
        String tharwaPath= args[2];
        String outputDir= args[11];

        ////////////////

        String DIA_En_EngDataPath =args[3];
        String DIA_En_ArDataPath =args[4];

        String MSA_En_EngDataPath =args[5];
        String MSA_En_ArDataPath =args [6];

        boolean isNormalized= Boolean.parseBoolean(args[7]);
        boolean isLemmatized= Boolean.parseBoolean(args[8]);
        boolean isDiacratized= Boolean.parseBoolean(args[9]);
        boolean includePOS= Boolean.parseBoolean(args[10]);

        String normalized="0";
        String lemmatized="tok";
        String diacratized="undiac";
        String POSTagged="0";

        if (isNormalized==true)
            normalized="AlefYaNorm";

        if (isLemmatized==true)
            lemmatized="lem";

        if (isDiacratized==true)
            diacratized="diac";

        if (includePOS==true)
            POSTagged="includePOS";

        String prefix= normalized+"_"+lemmatized+"_"+diacratized+"_"+POSTagged;

        //Creates TharwaDict from ExtractStats.Tharwa
        boolean diac= Boolean.getBoolean(args[9]);
        HashMap<Integer, Vector<MSAEnTuple>> tharwaDic = Tharwa.ConvertTharwaToDic(tharwaPath, isNormalized, diac);

        //Creates Alignmenent data dictionary for MSA-En and Egy-En separately.
        Object[] MSA_AlignmentdDics= ParallelCorpora.buildMSA_EnAllignDataDic(MSA_En_ArDataPath, MSA_En_EngDataPath, MSA_En_Alignment_Path, isNormalized, isDiacratized);
        Object[] DA_AlignmentdDics = ParallelCorpora.buildDA_EnAlignDic(DIA_En_ArDataPath, DIA_En_EngDataPath, DIA_En_Alignment_Path, isNormalized, isDiacratized);

        extractStats(MSA_AlignmentdDics, DA_AlignmentdDics, tharwaDic, outputDir+"/"+prefix, includePOS);


        System.out.println("DA_sim_En_sim_MSA_sim "+DA_sim_En_sim_MSA_sim_w_POS);
        System.out.println("DA_sim_En_sim_MSA_diff "+DA_sim_En_sim_MSA_diff_w_POS);
        System.out.println("DA_sim_En_diff_MSA_sim "+DA_sim_En_diff_MSA_sim_w_POS);
        System.out.println("DA_sim_En_diff_MSA_diff "+DA_sim_En_diff_MSA_diff_w_POS);

        System.out.println("DA_diff_En_sim_MSA_sim "+DA_diff_En_sim_MSA_sim_w_POS);
        System.out.println("DA_siff_En_sim_MSA_diff "+DA_diff_En_sim_MSA_diff_w_POS);
        System.out.println("DA_diff_En_diff_MSA_sim "+DA_diff_En_diff_MSA_sim_w_POS);

        System.out.println("DA_sim_En_sim_MSA_sim_w_POS"+DA_sim_En_sim_MSA_sim_w_POS);
        System.out.println("DA_sim_En_sim_MSA_diff_w_POS "+DA_sim_En_sim_MSA_diff_w_POS);
        System.out.println("DA_sim_En_diff_MSA_sim_w_POS"+DA_sim_En_diff_MSA_sim_w_POS);
        System.out.println("DA_sim_En_diff_MSA_diff_w_POS "+DA_sim_En_diff_MSA_diff_w_POS);

        System.out.println("DA_diff_En_sim_MSA_sim_w_POS "+DA_diff_En_sim_MSA_sim_w_POS);
        System.out.println("DA_siff_En_sim_MSA_diff_w_POS "+DA_diff_En_sim_MSA_diff_w_POS);
        System.out.println("DA_diff_En_diff_MSA_sim_w_POS "+DA_diff_En_diff_MSA_sim_w_POS);

        System.out.println("ExtractStats.Tharwa Size  "+ Tharwa.tharwaSize);

        Indexing.write_index_dic(Indexing.En_indexDic, outputDir + "/En_index_dic");
        Indexing.write_index_dic(Indexing.MSA_indexDic, outputDir + "/MSA_index_dic");
        Indexing.write_index_dic(Indexing.DIA_indexDic, outputDir + "/DIA_index_dic");
    }


    /**
     * This function extracts matching statistics from ExtractStats.Tharwa and Parallel Corpora
     * @param MSA_AlignmentDics
     * @param DA_AlignmentDics
     * @param tharwaDic
     * @param prefix
     * @param includePOSTag
     * @throws IOException
     */
    public static void extractStats (Object[] MSA_AlignmentDics, Object[] DA_AlignmentDics,
                                     HashMap<Integer, Vector<MSAEnTuple>> tharwaDic, String prefix, boolean includePOSTag) throws IOException
    {

        System.out.println("Extract Stats From Alignment Dics Started...");
        HashMap<Integer, HashMap<Integer, Integer>> En_MSA_AlignmentDic=new HashMap<Integer, HashMap<Integer, Integer>>();
        HashMap<Integer, HashMap<Integer, Integer>> En_DA_AlignmentDic=new HashMap<Integer, HashMap<Integer, Integer>>();

        HashMap<Integer, HashMap<Integer, Integer>> MSA_En_AlignmentDic=new HashMap<Integer, HashMap<Integer, Integer>>();
        HashMap<Integer, HashMap<Integer, Integer>> DA_En_AlignmentDic=new HashMap<Integer, HashMap<Integer, Integer>>();


        En_MSA_AlignmentDic= (HashMap<Integer, HashMap<Integer, Integer>>) MSA_AlignmentDics[0];
        MSA_En_AlignmentDic= (HashMap<Integer, HashMap<Integer, Integer>>) MSA_AlignmentDics[1];

        En_DA_AlignmentDic= (HashMap<Integer, HashMap<Integer, Integer>>) DA_AlignmentDics[0];
        DA_En_AlignmentDic= (HashMap<Integer, HashMap<Integer, Integer>>) DA_AlignmentDics[1];


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //output files containing extracted triplets
        //DA sim
        BufferedWriter DA_sim_En_sim_MSA_sim_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_sim-En_sim-MSA_sim",true));
        DA_sim_En_sim_MSA_sim_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_sim_En_sim_MSA_diff_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_sim-En_sim-MSA_diff",true));
        DA_sim_En_sim_MSA_diff_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_sim_En_diff_MSA_sim_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_sim-En_diff-MSA_sim",true));
        DA_sim_En_diff_MSA_sim_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_sim_En_diff_MSA_sim_1Col_Format_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_sim-En_diff-MSA_sim_1Col_format",true));
        DA_sim_En_diff_MSA_sim_1Col_Format_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_sim_En_diff_MSA_diff_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_sim-En_diff-MSA_diff",true));
        DA_sim_En_diff_MSA_diff_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        //DA diff
        BufferedWriter DA_diff_En_sim_MSA_sim_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_diff-En_sim-MSA_sim",true));
        DA_diff_En_sim_MSA_sim_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_diff_En_sim_MSA_diff_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_diff-En_sim-MSA_diff",true));
        DA_diff_En_sim_MSA_diff_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_diff_En_diff_MSA_sim_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_diff-En_diff-MSA_sim",true));
        DA_diff_En_diff_MSA_sim_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_diff_En_diff_MSA_diff_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_diff-En_diff-MSA_diff",true));
        DA_diff_En_diff_MSA_diff_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        ///////////////////////////// INCLUDE POS TAG in CREATING TRANSDICT ////////////////////////////////////////////////

        //DA sim
        BufferedWriter DA_sim_En_sim_MSA_sim_w_POS_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_sim-En_sim-MSA_sim_w_POS",true));
        DA_sim_En_sim_MSA_sim_w_POS_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_sim_En_sim_MSA_diff_w_POS_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_sim-En_sim-MSA_diff_w_POS",true));
        DA_sim_En_sim_MSA_diff_w_POS_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_sim_En_diff_MSA_sim_w_POS_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_sim-En_diff-MSA_sim_w_POS",true));
        DA_sim_En_diff_MSA_sim_w_POS_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_sim_En_diff_MSA_sim_w_POS_1Col_Format_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_sim-En_diff-MSA_sim_w_POS_1Col_format",true));
        DA_sim_En_diff_MSA_sim_w_POS_1Col_Format_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_sim_En_diff_MSA_diff_w_POS_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_sim-En_diff-MSA_diff_w_POS",true));
        DA_sim_En_diff_MSA_diff_w_POS_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        //DA diff
        BufferedWriter DA_diff_En_sim_MSA_sim_w_POS_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_diff-En_sim-MSA_sim_w_POS",true));
        DA_diff_En_sim_MSA_sim_w_POS_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_diff_En_sim_MSA_diff_w_POS_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_diff-En_sim-MSA_diff_w_POS",true));
        DA_diff_En_sim_MSA_diff_w_POS_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_diff_En_diff_MSA_sim_w_POS_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_diff-En_diff-MSA_sim_w_POS",true));
        DA_diff_En_diff_MSA_sim_w_POS_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");
        //BufferedWriter DA_diff_En_diff_MSA_diff_w_POS_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_diff-En_diff-MSA_diff_w_POS",true));
        //DA_diff_En_diff_MSA_diff_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");


        BufferedWriter transDicEntries_Dropped_Due_to_diff_POS_Writer= new BufferedWriter(new FileWriter(prefix+"_transDicEntries_Dropped_Due_to_diff_POS",true));
        transDicEntries_Dropped_Due_to_diff_POS_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        int counter=0;
        //Iterates on all ExtractStats.Tharwa entries to extract <<PARTIAL MATCHES>>
        for (int TharwaEGY: tharwaDic.keySet())
        {
            //System.out.println("ExtractStats.Tharwa EGY: "+get_DIA_Word(TharwaEGY));
            counter++;

            if (counter%100==0)
                System.out.println("Number of Processed Entries: "+ counter);


            //iterates on all MSA-En assigned to an EGY lemma
            for (MSAEnTuple MSAEnT: tharwaDic.get(TharwaEGY))
            {
                int TharwaEn= MSAEnT.getEn();
                int TharwaMSA= MSAEnT.getMSA();
                Set<Integer> TharwaIds= new HashSet<Integer> (MSAEnT.getIds());

                //System.out.println("ExtractStats.Tharwa MSA: " + get_MSA_Word(TharwaMSA));
                //System.out.println("ExtractStats.Tharwa En: "+get_En_Word(TharwaEn));

                //Checks DA_En_AlignmentDic
                //Found EGY word in Transdict--> DA_sim
                if (DA_En_AlignmentDic.containsKey(TharwaEGY))
                {

                    //System.out.println("EGY found in EGY-En data");
                    //fulfills the similarity condition on English
                    if (DA_En_AlignmentDic.get(TharwaEGY).containsKey(TharwaEn) && En_MSA_AlignmentDic.containsKey(TharwaEn))
                    {
                        //System.out.println("Found common En between EGY-En, MSA-En and ExtractStats.Tharwa");
                        //DA_sim, En_sim
                        if (En_MSA_AlignmentDic.get(TharwaEn).containsKey(TharwaMSA))
                        {
                            //System.out.println("Found common MSA");
                            if (includePOSTag==true)
                            {
                                int DA_POS= DA_En_AlignmentDic.get(TharwaEGY).get(TharwaEn);
                                int MSA_POS= En_MSA_AlignmentDic.get(TharwaEn).get(TharwaMSA);

                                //System.out.println("DA POS: " +get_POS(DA_POS));
                                //System.out.println("MSA POS: "+ get_POS(MSA_POS));

                                if (DA_POS== MSA_POS)
                                {
                                    //DA_sim, En_sim, MSA_sim w POS
                                    DA_sim_En_sim_MSA_sim_w_POS++;
                                    DA_sim_En_sim_MSA_sim_w_POS_Writer.write(MSAEnT.getIds()+"\t"+
                                            Indexing.get_DIA_Word(TharwaEGY)+"\t"+Indexing.get_En_Word(TharwaEn)+"\t"+Indexing.get_MSA_Word(TharwaMSA)+"\n");

                                }
                                else
                                {
                                    //if DA_POS is not equal to MSA_POS, then this triplet is not included in TransDict at all, hence does not count on the stats
                                    transDicEntries_Dropped_Due_to_diff_POS++;
                                    transDicEntries_Dropped_Due_to_diff_POS_Writer.write(MSAEnT.getIds()+"\t"+
                                            Indexing.get_DIA_Word(TharwaEGY)+"\t"+Indexing.get_En_Word(TharwaEn)+"\t"+Indexing.get_MSA_Word(TharwaMSA)+"\n");

                                }

                            }
                            else
                            {
                                //DA_sim, En_sim, MSA_sim
                                DA_sim_En_sim_MSA_sim++;
                                DA_sim_En_sim_MSA_sim_Writer.write(MSAEnT.getIds()+"\t"+
                                        Indexing.get_DIA_Word(TharwaEGY)+"\t"+Indexing.get_En_Word(TharwaEn)+"\t"+Indexing.get_MSA_Word(TharwaMSA)+"\n");

                            }

                        }
                        //MSA is not similar to ExtractStats.Tharwa
                        else
                        {

                            //System.out.println("MSA is not found in ExtractStats.Tharwa");
                            if (includePOSTag==true)
                            {
                                for (int transDicMSA:En_MSA_AlignmentDic.get(TharwaEn).keySet())
                                {
                                    int DA_POS= DA_En_AlignmentDic.get(TharwaEGY).get(TharwaEn);
                                    int MSA_POS= En_MSA_AlignmentDic.get(TharwaEn).get(transDicMSA);

                                    //System.out.println("DA POS: " +get_POS(DA_POS));
                                    //System.out.println("MSA POS: "+get_POS(MSA_POS));

                                    if (DA_POS== MSA_POS)
                                    {
                                        //qualifies for being an entry in TransDict
                                        DA_sim_En_sim_MSA_diff_w_POS++;
                                        DA_sim_En_sim_MSA_diff_w_POS_Writer.write(MSAEnT.getIds()+"\t"+
                                                Indexing.get_DIA_Word(TharwaEGY)+"\t"+Indexing.get_En_Word(TharwaEn)+"\t"+Indexing.get_MSA_Word(transDicMSA)+"\n");
                                        DA_sim_En_sim_MSA_diff_w_POS_Writer.write("\n");
                                    }
                                }
                            }
                            else
                            {
                                //DA_sim, En_sim, MSA_diff
                                DA_sim_En_sim_MSA_diff++;
                                for (int transDicMSA:En_MSA_AlignmentDic.get(TharwaEn).keySet())
                                {
                                    DA_sim_En_sim_MSA_diff_Writer.write(MSAEnT.getIds()+"\t"+
                                            Indexing.get_DIA_Word(TharwaEGY)+"\t"+Indexing.get_En_Word(TharwaEn)+"\t"+Indexing.get_MSA_Word(transDicMSA)+"\n");
                                }
                                DA_sim_En_sim_MSA_diff_Writer.write("\n");


                            }

                        }
                    }
                    else
                    {
                        //System.out.println("Common Eng is not found");
                        //DA_sim, Eng_diff
                        if (MSA_En_AlignmentDic.containsKey(TharwaMSA))
                        {
                           // System.out.println("Found common MSA");
                            Vector<Integer> EnAlignedToDA= new Vector<Integer>(DA_En_AlignmentDic.get(TharwaEGY).keySet());
                            Vector<Integer> EnAlignedToMSA= new Vector<Integer>(MSA_En_AlignmentDic.get(TharwaMSA).keySet());

                            EnAlignedToDA.retainAll(EnAlignedToMSA);

                            //There exists at least one Eng lemma commonly aligned to both MSA and DA word.
                            if (EnAlignedToDA.size()>0)
                            {
                                for (int transDicEn:EnAlignedToDA)
                                {
                                    if (includePOSTag==true)
                                    {
                                        int DA_POS= DA_En_AlignmentDic.get(TharwaEGY).get(transDicEn);
                                        int MSA_POS= MSA_En_AlignmentDic.get(TharwaMSA).get(transDicEn);

                                        if (DA_POS== MSA_POS)
                                        {
                                            DA_sim_En_diff_MSA_sim_w_POS++;
                                            DA_sim_En_diff_MSA_sim_w_POS_Writer.write(MSAEnT.getIds()+"\t"+
                                                    Indexing.get_DIA_Word(TharwaEGY)+"\t"+Indexing.get_En_Word(transDicEn)+"\t"+Indexing.get_MSA_Word(TharwaMSA)+"\n");
                                            DA_sim_En_diff_MSA_sim_w_POS_Writer.write("\n");

                                            //To avoid replicating rows
                                            if (!DA_sim_En_diff_MSA_sim_w_POS_found_rows.containsKey(TharwaEGY))
                                            {
                                              Set<Integer> idArrayList= new HashSet<Integer>();
                                                idArrayList.addAll(TharwaIds);

                                                ArrayList<Integer> EnArrayList= new ArrayList<Integer>();
                                                EnArrayList.add(transDicEn);

                                                ArrayList<Integer> POSArrayList= new ArrayList<Integer>();
                                                POSArrayList.add(MSA_POS);

                                                ArrayList<Object> tempArrayList= new ArrayList<Object>();

                                                tempArrayList.add(EnArrayList);
                                                tempArrayList.add(idArrayList);
                                                tempArrayList.add(POSArrayList);

                                                HashMap<Integer, ArrayList<Object>> MSAHashMap= new HashMap<Integer, ArrayList<Object>>();
                                                MSAHashMap.put(TharwaMSA, tempArrayList);

                                                DA_sim_En_diff_MSA_sim_w_POS_found_rows.put(TharwaEGY, MSAHashMap);

                                            }
                                            else {
                                                if (!DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(TharwaEGY).containsKey(TharwaMSA)) {
                                                    Set<Integer> idArrayList = new HashSet<Integer>();
                                                    idArrayList.addAll(TharwaIds);

                                                    ArrayList<Integer> EnArrayList = new ArrayList<Integer>();
                                                    EnArrayList.add(transDicEn);

                                                    ArrayList<Integer> POSArrayList= new ArrayList<Integer>();
                                                    POSArrayList.add(MSA_POS);

                                                    ArrayList<Object> tempArrayList = new ArrayList<Object>();
                                                    tempArrayList.add(EnArrayList);
                                                    tempArrayList.add(idArrayList);
                                                    tempArrayList.add(POSArrayList);


                                                    DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(TharwaEGY).put(TharwaMSA, tempArrayList);
                                                } else {
                                                    if (! ((ArrayList<Integer>) DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(TharwaEGY).get(TharwaMSA).get(0)).contains(transDicEn)) {

                                                        ((ArrayList<Integer>) DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(TharwaEGY).get(TharwaMSA).get(0)).add(transDicEn);
                                                        ((ArrayList<Integer>) DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(TharwaEGY).get(TharwaMSA).get(2)).add(MSA_POS);
                                                    }
                                                    else {

                                                        System.out.println("Current IDs: " + DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(TharwaEGY).get(TharwaMSA).get(1));
                                                        System.out.println("New IDs: " + TharwaIds);
                                                        Set<Integer> Ids2Add= TharwaIds;
                                                        Ids2Add.removeAll((Set<Integer>)DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(TharwaEGY).get(TharwaMSA).get(1));

                                                        System.out.println("Set of Ids to add: "+ Ids2Add);

                                                        if (Ids2Add.size() > 0)
                                                            ((Set<Integer>)DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(TharwaEGY).get(TharwaMSA).get(1)).addAll(Ids2Add);
                                                    }

                                                }
                                            }
                                        }

                                    }

                                    else
                                    {
                                        DA_sim_En_diff_MSA_sim++;
                                        DA_sim_En_diff_MSA_sim_Writer.write(MSAEnT.getIds()+"\t"+
                                                Indexing.get_DIA_Word(TharwaEGY)+"\t"+Indexing.get_En_Word(transDicEn)+"\t"+Indexing.get_MSA_Word(TharwaMSA)+"\n");
                                        DA_sim_En_diff_MSA_sim_Writer.write("\n");


                                        //To avoid replicating rows
                                        if (!DA_sim_En_diff_MSA_sim_found_rows.containsKey(TharwaEGY))
                                        {
                                            Set<Integer> idArrayList= new HashSet<Integer>();
                                            idArrayList.addAll(TharwaIds);

                                            Set<Integer> EnArrayList= new HashSet<Integer>();
                                            EnArrayList.add(transDicEn);

                                            ArrayList<Set<Integer>> tempArrayList= new ArrayList<Set<Integer>>();
                                            tempArrayList.add(EnArrayList);
                                            tempArrayList.add(idArrayList);

                                            HashMap<Integer, ArrayList<Set<Integer>>> MSAHashMap= new HashMap<Integer, ArrayList<Set<Integer>>>();
                                            MSAHashMap.put(TharwaMSA, tempArrayList);

                                            DA_sim_En_diff_MSA_sim_found_rows.put(TharwaEGY, MSAHashMap);

                                        }
                                        else
                                        {
                                            if (!DA_sim_En_diff_MSA_sim_found_rows.get(TharwaEGY).containsKey(TharwaMSA))
                                            {
                                                Set<Integer> idArrayList = new HashSet<Integer>();
                                                idArrayList.addAll(TharwaIds);

                                                Set<Integer> EnArrayList = new HashSet<Integer>();
                                                EnArrayList.add(transDicEn);

                                                ArrayList<Set<Integer>> tempArrayList = new ArrayList<Set<Integer>>();
                                                tempArrayList.add(EnArrayList);
                                                tempArrayList.add(idArrayList);

                                                DA_sim_En_diff_MSA_sim_found_rows.get(TharwaEGY).put(TharwaMSA, tempArrayList);
                                            } else
                                            {
                                                if(!DA_sim_En_diff_MSA_sim_found_rows.get(TharwaEGY).get(TharwaMSA).get(0).contains(transDicEn))
                                                    DA_sim_En_diff_MSA_sim_found_rows.get(TharwaEGY).get(TharwaMSA).get(0).add(transDicEn);
                                                else
                                                {
                                                    Set<Integer> Ids2Add= TharwaIds;
                                                    Ids2Add.removeAll(DA_sim_En_diff_MSA_sim_found_rows.get(TharwaEGY).get(TharwaMSA).get(1));


                                                    if (Ids2Add.size()>0)
                                                        DA_sim_En_diff_MSA_sim_found_rows.get(TharwaEGY).get(TharwaMSA).get(1).addAll(Ids2Add);
                                                }


                                            }

                                        }
                                    }

                                }
                            }
                        }
                        else
                        {
                            //System.out.println("Common MSA is not found");
                            //DA_sim, Eng_diff, MSA_diff

                            for (int transDicEn:DA_En_AlignmentDic.get(TharwaEGY).keySet())
                            {
                                if (En_MSA_AlignmentDic.containsKey(transDicEn))
                                {
                                    for (int transDicMSA:En_MSA_AlignmentDic.get(transDicEn).keySet())
                                    {
                                        if (includePOSTag==true)
                                        {
                                            int DA_POS= DA_En_AlignmentDic.get(TharwaEGY).get(transDicEn);
                                            int MSA_POS= En_MSA_AlignmentDic.get(transDicEn).get(transDicMSA);

                                            if (DA_POS== MSA_POS)
                                            {
                                                DA_sim_En_diff_MSA_diff_w_POS++;
                                                DA_sim_En_diff_MSA_diff_w_POS_Writer.write(MSAEnT.getIds()+"\t"+
                                                        Indexing.get_DIA_Word(TharwaEGY)+"\t"+Indexing.get_En_Word(transDicEn)+"\t"+Indexing.get_MSA_Word(transDicMSA)+"\n");
                                                DA_sim_En_diff_MSA_diff_w_POS_Writer.write("\n");
                                            }

                                        }
                                        else
                                        {
                                            DA_sim_En_diff_MSA_diff++;
                                            DA_sim_En_diff_MSA_diff_Writer.write(MSAEnT.getIds()+"\t"+
                                                    Indexing.get_DIA_Word(TharwaEGY)+"\t"+Indexing.get_En_Word(transDicEn)+"\t"+Indexing.get_MSA_Word(transDicMSA)+"\n");
                                            DA_sim_En_diff_MSA_diff_Writer.write("\n");
                                        }
                                    }


                                }
                            }

                        }
                    }

                }


                ////Extract Triples from TransDic which have not DA entry matched to TharwaDic
                //System.out.println("Writing DA diff");

                //check if MSA matches to TransDict
                if (MSA_En_AlignmentDic.containsKey(TharwaMSA))
                {
                    //System.out.println("Common MSA");
                    //DA_diff_MSA_sim

                    if (MSA_En_AlignmentDic.get(TharwaMSA).containsKey(TharwaEn) && En_DA_AlignmentDic.containsKey(TharwaEn))
                    {
                       // System.out.println("Common En");
                        //DA_diff_En_sim_MSA_sim
                        for (int transDicDA: En_DA_AlignmentDic.get(TharwaEn).keySet())
                        {
                            if (includePOSTag)
                            {
                                int DA_POS= En_DA_AlignmentDic.get(TharwaEn).get(transDicDA);
                                int MSA_POS= MSA_En_AlignmentDic.get(TharwaMSA).get(TharwaEn);

                                if (DA_POS==MSA_POS)
                                {
                                    DA_diff_En_sim_MSA_sim_w_POS++;
                                    DA_diff_En_sim_MSA_sim_w_POS_Writer.write(MSAEnT.getIds()+"\t"
                                            +Indexing.get_DIA_Word(transDicDA)+"\t"+Indexing.get_En_Word(TharwaEn)+"\t"+Indexing.get_MSA_Word(TharwaMSA)+"\n\n");
                                }

                            }
                            else
                            {
                                DA_diff_En_sim_MSA_sim++;
                                DA_diff_En_sim_MSA_sim_Writer.write(MSAEnT.getIds()+"\t"+
                                        Indexing.get_DIA_Word(transDicDA)+"\t"+Indexing.get_En_Word(TharwaEn)+"\t"+Indexing.get_MSA_Word(TharwaMSA)+"\n\n");
                            }
                        }
                    }
                    else
                    {
                        //System.out.println("Common En is not found");
                        //DA_diff_MSA_sim_En_diff
                        for (int transDicEn: MSA_En_AlignmentDic.get(TharwaMSA).keySet())
                        {
                            if (En_DA_AlignmentDic.containsKey(transDicEn))
                            {
                                for (int transDicDA: En_DA_AlignmentDic.get(transDicEn).keySet())
                                {
                                    if (includePOSTag==true)
                                    {
                                        int DA_POS= En_DA_AlignmentDic.get(transDicEn).get(transDicDA);
                                        int MSA_POS= MSA_En_AlignmentDic.get(TharwaMSA).get(transDicEn);

                                        if (DA_POS== MSA_POS)
                                        {
                                            DA_diff_En_diff_MSA_sim_w_POS++;
                                            DA_diff_En_diff_MSA_sim_w_POS_Writer.write(MSAEnT.getIds()+"\t"+
                                                    Indexing.get_DIA_Word(transDicDA)+"\t"+Indexing.get_En_Word(transDicEn)+"\t"+Indexing.get_MSA_Word(TharwaMSA)+"\n\n");
                                        }

                                    }
                                    else
                                    {
                                        DA_diff_En_diff_MSA_sim++;
                                        DA_diff_En_diff_MSA_sim_Writer.write(MSAEnT.getIds()+"\t"+
                                                Indexing.get_DIA_Word(transDicDA)+"\t"+Indexing.get_En_Word(transDicEn)+"\t"+Indexing.get_MSA_Word(TharwaMSA)+"\n\n");
                                    }
                                }

                            }
                        }

                    }

                }

                else
                {

                   // System.out.println("Common MSA is not found");
                    //DA_diff_MSA_diff

                    if (En_DA_AlignmentDic.containsKey(TharwaEn) && En_MSA_AlignmentDic.containsKey(TharwaEn))
                    {
                        //DA_diff_MSA_diff_En_sim
                        for (int transDicMSA: En_MSA_AlignmentDic.get(TharwaEn).keySet())
                        {
                            for (int transDicDA: En_DA_AlignmentDic.get(TharwaEn).keySet())
                            {
                                if (includePOSTag==true)
                                {
                                    int DA_POS=En_DA_AlignmentDic.get(TharwaEn).get(transDicDA);
                                    int MSA_POS= En_MSA_AlignmentDic.get(TharwaEn).get(transDicMSA);

                                    if (DA_POS== MSA_POS)
                                    {
                                        DA_diff_En_sim_MSA_diff_w_POS++;
                                        DA_diff_En_sim_MSA_diff_w_POS_Writer.write(MSAEnT.getIds()+"\t"+
                                                Indexing.get_DIA_Word(transDicDA)+"\t"+Indexing.get_En_Word(TharwaEn)+"\t"+Indexing.get_MSA_Word(transDicMSA)+"\n\n");
                                    }

                                }
                                else
                                {
                                    DA_diff_En_sim_MSA_diff++;
                                    DA_diff_En_sim_MSA_diff_Writer.write(MSAEnT.getIds()+"\t"+
                                            Indexing.get_DIA_Word(transDicDA)+"\t"+Indexing.get_En_Word(TharwaEn)+"\t"+Indexing.get_MSA_Word(transDicMSA)+"\n\n");
                                }
                            }
                        }
                    }
                    else
                    {
                        //DA_diff_MSA_diff_En_diff
                        //This output does not seem to be helpful and there is not any feasible way yo get it

                    }

                }

            }
        }

        //write down the 1-Col format with POS
        for (int egy:DA_sim_En_diff_MSA_sim_w_POS_found_rows.keySet())
        {
            for (int msa:DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(egy).keySet())
            {
                DA_sim_En_diff_MSA_sim_w_POS_1Col_Format_Writer.write(Indexing.get_DIA_Word(egy)+"\t"+Indexing.get_MSA_Word(msa)+"\t");
                for (int i=0;i<((ArrayList<Integer>)DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(egy).get(msa).get(0)).size();i++)
                {
                    DA_sim_En_diff_MSA_sim_w_POS_1Col_Format_Writer.write(Indexing.get_En_Word(((ArrayList<Integer>) DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(egy).get(msa).get(0)).get(i))+"/"+
                            Indexing.get_POS(((ArrayList<Integer>) DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(egy).get(msa).get(2)).get(i))+",");
                }
                DA_sim_En_diff_MSA_sim_w_POS_1Col_Format_Writer.write("\t"+DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(egy).get(msa).get(1)+"\n");
            }
        }

        //write down the 1-Col format
        for (int egy:DA_sim_En_diff_MSA_sim_found_rows.keySet())
        {
            for (int msa:DA_sim_En_diff_MSA_sim_found_rows.get(egy).keySet())
            {
                DA_sim_En_diff_MSA_sim_1Col_Format_Writer.write(Indexing.get_DIA_Word(egy)+"\t"+Indexing.get_MSA_Word(msa)+"\t");
                for (int en:DA_sim_En_diff_MSA_sim_found_rows.get(egy).get(msa).get(0))
                {
                    DA_sim_En_diff_MSA_sim_1Col_Format_Writer.write(Indexing.get_En_Word(en)+",");
                }
                DA_sim_En_diff_MSA_sim_1Col_Format_Writer.write("\t" + DA_sim_En_diff_MSA_sim_found_rows.get(egy).get(msa).get(1) + "\n");
            }
        }

        DA_sim_En_sim_MSA_sim_Writer.flush();
        DA_sim_En_sim_MSA_sim_Writer.close();
        DA_sim_En_sim_MSA_sim_Writer.close();

        DA_sim_En_diff_MSA_diff_Writer.flush();
        DA_sim_En_diff_MSA_diff_Writer.close();

        DA_sim_En_sim_MSA_diff_Writer.flush();
        DA_sim_En_sim_MSA_diff_Writer.close();

        DA_sim_En_diff_MSA_sim_Writer.flush();
        DA_sim_En_diff_MSA_sim_Writer.close();

        /////////////////////////////////////
        DA_diff_En_sim_MSA_sim_Writer.flush();
        DA_diff_En_sim_MSA_sim_Writer.close();

        DA_diff_En_diff_MSA_diff_Writer.flush();
        DA_diff_En_diff_MSA_diff_Writer.close();

        DA_diff_En_sim_MSA_diff_Writer.flush();
        DA_diff_En_sim_MSA_diff_Writer.close();

        DA_diff_En_diff_MSA_sim_Writer.flush();
        DA_diff_En_diff_MSA_sim_Writer.close();

        /////////////////////////////////////
        DA_sim_En_sim_MSA_sim_w_POS_Writer.flush();
        DA_sim_En_sim_MSA_sim_w_POS_Writer.close();

        DA_sim_En_diff_MSA_diff_w_POS_Writer.flush();
        DA_sim_En_diff_MSA_diff_w_POS_Writer.close();

        DA_sim_En_sim_MSA_diff_w_POS_Writer.flush();
        DA_sim_En_sim_MSA_diff_w_POS_Writer.close();

        DA_sim_En_diff_MSA_sim_w_POS_Writer.flush();
        DA_sim_En_diff_MSA_sim_w_POS_Writer.close();

        /////////////////////////////////////
        DA_diff_En_sim_MSA_sim_w_POS_Writer.flush();
        DA_diff_En_sim_MSA_sim_w_POS_Writer.close();

        //DA_diff_En_diff_MSA_diff_w_POS_Writer.flush();
        //DA_diff_En_diff_MSA_diff_w_POS_Writer.close();

        DA_diff_En_sim_MSA_diff_w_POS_Writer.flush();
        DA_diff_En_sim_MSA_diff_w_POS_Writer.close();

        DA_diff_En_diff_MSA_sim_w_POS_Writer.flush();
        DA_diff_En_diff_MSA_sim_w_POS_Writer.close();

        DA_sim_En_diff_MSA_sim_1Col_Format_Writer.flush();
        DA_sim_En_diff_MSA_sim_1Col_Format_Writer.close();

        DA_sim_En_diff_MSA_sim_w_POS_1Col_Format_Writer.flush();
        DA_sim_En_diff_MSA_sim_w_POS_1Col_Format_Writer.close();

        transDicEntries_Dropped_Due_to_diff_POS_Writer.flush();
        transDicEntries_Dropped_Due_to_diff_POS_Writer.close();

        System.out.println("Extract Stats Finished!\n");
    }

}
