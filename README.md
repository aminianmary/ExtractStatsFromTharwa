#**ExtractStatsFromTharwa**
This code package contains two main subpackages:

#Triangulation:
Output of this class provides triplets extracted from bitext (MSA-En and EGY-En) in the following format:

        EGY     MSA     Eng_equivalents_list    [Tharwa IDs]

Please notice that files are distinguished with different prefixes which specifies the preprocessing steps and criteria used to generate triplets. This directory contains files with the following suffixes:

AlefYaNorm_lem_diac_POS
AlefYaNorm_lem_undiac_POS
AlefYaNorm_lem_diac_0
AlefYaNorm_lem_undiac_0

0_lem_diac_POS
0_lem_undiac_POS
0_lem_diac_0
0_lem_undiac_0

####Data Format Description
Files in this directory contain lists of triplets extracted from parallel data which have "Similar EGY" AND "Similar MSA" BUT "Different English equivalent" compared to Tharwa. Hence, list of English equivalents provided in each file can be used to indicate other possible English equivalents of each EGY-MSA pairs.
When POS is used as the matching condition (files with "POS" in their prefix), the POS tag of EGY-MSA pair is listed with "/" along with English equivalents. In other word, these tags indicate POS of the EGY-MSA pair when both was aligned to this English equivalent in the alignment data. It is worth noting that these POS tags might be different. MADAMIRA is used for POS tagging.

The last column in these files shows all Tharwa IDs (entries) with this EGY-MSA pair.


####Prefix Description
"AlefYaNorm" indicates that all different kinds of Alef ("|,<,>,{,`") and Ya ("Y, y") in the text are normalized into "A" and "y" respectively. If normalization is not performed, "0" is placed in this position.
"lem" means that parallel data are in the lemma format. All provided files are extracted from lemmatized bitexts.
"diac" states data has been diacratizd. Similarly, "undiac" is used when data used to extract triplets is not diacritized.
"POS" indicates that POS matching criteria between EGY and MSA in each triplets is applied to make triplets. "0" in the place refers to the case that POS matching condition is not used.

It is to be emphasized that level of preprocessing affects number of triplets matched against Tharwa.

#Linking Tharwa to Parallel Corpora

Output of this class contain example sentences extracted from parallel data (MSA-En and EGY-En) for Tharwa entries. Aligned English (extracted from automatic word alignment) is matched against equivalent English column in Tharwa (column 8) to extract MSA-En and EGY-En sentences separately. As the result, for each entry in Tharwa in the form EGY-En-MSA, we extract two sets of sentences for EGY-En and MSA-En with En equivalent in common.

####Preprocessing

Files have different prefixes which show the preprocessing steps. It is worth noting that different preprocessing steps affect number of example sentences matched against Tharwa. Following show list of prefixes:

AlefYaNorm_lem_diac
AlefYaNorm_lem_undiac

0_lem_diac
0_lem_undiac

"AlefYaNorm" indicates that all different kinds of Alef ("|,<,>,{,`") and Ya (Y, y) in the text are normalized into "A" and "y" respectively. If normalization is not performed, "0" is placed in this position.
"lem" means that parallel data are in the lemma format. All provided files are extracted from lemmatized bitexts.
"diac" states data has been diacratizd. Similarly, "undiac" is used when data used to extract triplets is not diacritized.

####Data Format
Tharwa entries and extracted examples for them are shown in the following tab separated format:

[Tharwa ID] Tharwa_EGY  Tharwa_En Tharwa_MSA  S1;;S2;;S3;;S4;; ... ;;S19;;S20##S'1;;S'2;;S'3;;S'4;; ... ;;S'19;;S'20

The 4th column above contains sentences extracted from parallel data. The first sequence of sentences before "#" sign, shown with S1,...,S20, are sentences containing "Tharwa_EGY" in the EGY side and aligned "Tharwa_En" in the En side. Similarly, the second sequence of sentences, shown with S'1,...,S'20 refer to sentences with "Tharwa_MSA" in the MSA side and aligned "Tharwa_En" in the En side.

For convenience, each sentence in the sequence is shown in the following format:

Sentence_Number/Arabic word offset in the Arabic side/English word offset in the English side

Please note that all offsets start at 0. In case no EGY-En sentence is found for the Tharwa entry, the sequence of sentences will start with "##".

Parallel data used for the extraction process are also placed in the "resource" subdirectory. Data include Bolt (EGY-En) and Gale (MSA-En) parallel data.

*Maryam Aminian*

*Jan 8, 2016*