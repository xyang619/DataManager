package cn.ac.picb.young.dm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class VCF2Eigen {
	public static void writeIndfile(String line, String filename, String pop) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(filename));
			String[] cols = line.split("\\s+");
			for (int i = 9; i < cols.length; i++) {
				bw.write(cols[i] + "\tU\t" + pop);
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Set<String> readRef(String filename) {
		Set<String> ref = new HashSet<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null && line.length() > 0) {
				String[] cols = line.split("\\s+");
				ref.add(cols[0]);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ref;
	}

	public static void convert(String vcfFile, String outPrefix, String pop, String refFile) {
		BufferedReader br = null;
		BufferedWriter genoBw = null;
		BufferedWriter snpBw = null;
		Set<String> ref = null;
		if (refFile != null) {
			ref = readRef(refFile);
		}
		String indfile = outPrefix + ".ind";
		String genofile = outPrefix + ".geno";
		String snpfile = outPrefix + ".snp";
		try {
			if (vcfFile.toUpperCase().endsWith("VCF.GZ")) {
				br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(vcfFile))));
			} else {
				br = new BufferedReader(new FileReader(vcfFile));
			}
			genoBw = new BufferedWriter(new FileWriter(genofile));
			snpBw = new BufferedWriter(new FileWriter(snpfile));
			StringBuilder snpRow = new StringBuilder();
			StringBuilder genoRow = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null && line.length() > 0) {
				if (line.startsWith("##")) {
					continue;
				}
				if (line.startsWith("#CHROM")) {
					writeIndfile(line, indfile, pop);
				} else {
					String[] cols = line.split("\\s+");
					// skip tri-allelic SNPs
					if (cols[4].length() > 1) {
						continue;
					}
					if ((ref == null) || (ref.contains(cols[2]))) {
						snpRow.append(cols[2]).append("\t").append(cols[0]).append("\t0.0\t").append(cols[1])
								.append("\t").append(cols[3]).append("\t").append(cols[4]);
						snpBw.write(snpRow.toString());
						snpBw.newLine();
						snpRow.setLength(0);
						for (int i = 9; i < cols.length; i++) {
							// missing
							if (cols[i].charAt(0) == '.' || cols[i].charAt(2) == '.') {
								genoRow.append("9");
							} else {
								genoRow.append((2 * '1' - cols[i].charAt(0) - cols[i].charAt(2)));
							}
						}
						genoBw.write(genoRow.toString());
						genoBw.newLine();
						genoRow.setLength(0);
					}
				}
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

	// public static void main(String[] args) {
	// if (args.length > 3) {
	// convert(args[0], args[1], args[2], args[3]);
	// } else if (args.length > 2) {
	// convert(args[0], args[1], args[2], null);
	// } else if (args.length > 1) {
	// convert(args[0], args[1], "Han", null);
	// } else {
	// System.out.println("Usage: java -jar VCF2Eigen.jar <inpfile> <outPrefix> [poplabel] [refSnpfile]");
	// }
	// }
}
