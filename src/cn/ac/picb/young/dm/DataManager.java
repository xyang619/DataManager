package cn.ac.picb.young.dm;

public class DataManager {

	public static void help(int index) {
		System.out.println("DataManager version 0.1.0");
		switch (index) {
		case 0:
			System.out.println("General usage: java -jar DataManager.jar tool <arguments> [options]");
			System.out.println("Current available tools: ");
			System.out.println("\tinp2eigen\tConvert data in inp format into EIGENSTRAT format");
			System.out.println("\tvcf2eigen\tConvert data in VCF format into EIGENSTRAT format");
			//System.out.println("\teigen2ped\tConvert data in EIGENSTRAT format into plink ped format");
			break;
		case 1:
			System.out.println("Usage for tool inp2eigen:");
			System.out.println("java -jar DataManager.jar inp2eigen <inpfile> <outPrefix> [poplabel]");
			System.out.println("\tinpfile:\tInpfile for converting, can be compressed as XX.inp.gz");
			System.out.println("\toutPrefix:\tPrefix for output files");
			System.out.println("\tpoplabel:\tPopulation label");
			break;
		case 2:
			System.out.println("Usage for tool vcf2eigen:");
			System.out.println("java -jar DataManager.jar vcf2eigen <vcffile> <outPrefix> [poplabel] [refSnpfile]");
			System.out.println("\tvcffile:\tVcffile for converting, can be compressed as XX.vcf.gz");
			System.out.println("\toutPrefix:\tPrefix for output files");
			System.out.println("\tpoplabel:\tPopulation label");
			System.out.println("\trefSnpfile:\tReference SNPfile");
			break;
		case 3:
			break;
		default:
			System.out.println("Unknown tool");
		}

	}

	public static void main(String[] args) {
		if (args.length == 0) {
			help(0);
		} else {
			if (args[0].equals("inp2eigen")) {
				switch (args.length) {
				case 1:
				case 2:
					help(1);
					break;
				case 3:
					Inp2Eigen.convert(args[1], args[2], "Han");
					break;
				default:
					Inp2Eigen.convert(args[1], args[2], args[3]);
				}

			} else if (args[0].equals("vcf2eigen")) {
				switch (args.length) {
				case 1:
				case 2:
					help(2);
					break;
				case 3:
					VCF2Eigen.convert(args[1], args[2], "Han", null);
					break;
				case 4:
					VCF2Eigen.convert(args[1], args[2], args[3], null);
				default:
					VCF2Eigen.convert(args[1], args[2], args[3], args[4]);
				}
			} else {
				help(0);
			}
		}
	}
}
