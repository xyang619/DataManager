package cn.ac.picb.young.dm;

/*
 * Formats
 * 1. Inp format:
 * dbSNP_ID Chromosome Position Strand Allele HGDP00991 HGDP01029 HGDP01032 HGDP00992 HGDP01036
 * rs3094315       1       752566  +       A/G     A       A       A       H       H
 * 2. Eigenstrat format:
 * 1)Indfile
 * HGDP00991 U San
 * 2)Snpfile
 * rs3094315 1 0.0 752566 A G
 * 3)Genofile:
 * 22211
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class Inp2Eigen {
	public static void complement(String[] alleles) {
		for (int i = 0; i < alleles.length; i++) {
			if (alleles[i].equals("A")) {
				alleles[i] = "T";
			} else if (alleles[i].endsWith("C")) {
				alleles[i] = "G";
			} else if (alleles[i].endsWith("G")) {
				alleles[i] = "C";
			} else if (alleles[i].endsWith("T")) {
				alleles[i] = "A";
			} else {
				alleles[i] = "N";
			}
		}
	}

	public static void writeIndfile(String line, String filename, String pop) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(filename));
			String[] cols = line.split("\\s+");
			for (int i = 5; i < cols.length; i++) {
				bw.write(cols[i] + "\tU\t" + pop);
				bw.newLine();
			}
			bw.flush();
			bw.close();
			//System.out.println("write individual information to " + filename);
		} catch (FileNotFoundException e) {
			System.out.println(filename + " not found");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void convert(String inpfile, String outPrefix, String pop) {
		BufferedReader br = null;
		BufferedWriter genoBw = null;
		BufferedWriter snpBw = null;
		String indfile = outPrefix + ".ind";
		String genofile = outPrefix + ".geno";
		String snpfile = outPrefix + ".snp";
		try {
			if (inpfile.toUpperCase().endsWith("INP.GZ")) {
				br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(inpfile))));
			} else {
				br = new BufferedReader(new FileReader(inpfile));
			}
			String line = br.readLine();
			writeIndfile(line, indfile, pop);
			genoBw = new BufferedWriter(new FileWriter(genofile));
			snpBw = new BufferedWriter(new FileWriter(snpfile));
			StringBuilder snpRow = new StringBuilder();
			StringBuilder genoRow = new StringBuilder();
			while ((line = br.readLine()) != null && line.length() > 0) {
				String[] cols = line.split("\\s+");
				String[] alleles = cols[4].split("/");
				snpRow.append(cols[0]).append("\t").append(cols[1]).append("\t0.0\t").append(cols[2]).append("\t");
				// reverse strand, change to forward strand
				if (cols[3].equals("-")) {
					complement(alleles);
				}
				boolean flip = false;
				if (alleles[0].compareToIgnoreCase(alleles[1]) > 0) {
					flip = true;
					snpRow.append(alleles[1]).append("\t").append(alleles[0]);
				} else {
					snpRow.append(alleles[0]).append("\t").append(alleles[1]);
				}
				snpBw.write(snpRow.toString());
				snpBw.newLine();
				snpRow.setLength(0);
				for (int i = 5; i < cols.length; i++) {
					if (flip) {
						if (cols[i].equals("A")) {
							genoRow.append("0");
						} else if (cols[i].equals("H")) {
							genoRow.append("1");
						} else if (cols[i].equals("B")) {
							genoRow.append("2");
						} else {
							genoRow.append("9");
						}
					} else {
						if (cols[i].equals("A")) {
							genoRow.append("2");
						} else if (cols[i].equals("H")) {
							genoRow.append("1");
						} else if (cols[i].equals("B")) {
							genoRow.append("0");
						} else {
							genoRow.append("9");
						}
					}
				}
				genoBw.write(genoRow.toString());
				genoBw.newLine();
				genoRow.setLength(0);
			}
			snpBw.flush();
			snpBw.close();
			genoBw.flush();
			genoBw.close();
			br.close();
			System.out.println("Finished converting, find results in " + outPrefix + ".geno, " + outPrefix
					+ ".snp and " + outPrefix + ".ind");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	public static void main(String[] args) {
//
//		if (args.length > 2) {
//			convert(args[0], args[1], args[2]);
//		} else if (args.length > 1) {
//			convert(args[0], args[1], "Han");
//		} else {
//			System.out.println("Usage: java -jar Inp2Eigen.jar <inpfile> <outPrefix> [poplabel]");
//		}
//	}
}
