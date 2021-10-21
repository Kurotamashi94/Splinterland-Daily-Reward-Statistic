package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

import main.CardDirectory;
import main.RewardLibrary;
import main.ExportCSV;

public class RewardStatistic {

	public static int totalSize;
	public static int accountRun;
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Vector<RewardLibrary> consolidateReward = new Vector<RewardLibrary>();
		Vector<String> accDirectory = new Vector<String>();
		WelcomeMessage();
		accDirectory = ReadUserFromFile();
		if (!accDirectory.isEmpty()) {
			System.out.println("###########################################################\n");
			for (String userName : accDirectory) {
				GetClaimTrxID(userName, consolidateReward);
			}
			TotalLoot(consolidateReward);
			ExportCSV exportFile = new ExportCSV();
			exportFile.ExportCSV(consolidateReward);
		}
		EndMessage();
	}

	public static void GetClaimTrxID(String userName, Vector<RewardLibrary> consolidateReward) throws Exception {
		Vector<String> tempTrx = new Vector<String>();
		URL url = new URL("https://api.splinterlands.io/players/quests?username=" + userName);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line, trxID = null;
		while ((line = bufferedReader.readLine()) != null) {
			for (String s : line.split(",")) {
				tempTrx.add(s);
			}
		}
		bufferedReader.close();
		con.disconnect();

		if (tempTrx.contains("\"player\":\"" + userName + "\"")) {
			accountRun++;
			for (int b = 0; b < tempTrx.size(); b++) {
				if (tempTrx.elementAt(b).contains("\"claim_trx_id\":")) {
					if (tempTrx.elementAt(b).contains("null")) {
						accountRun--;
						System.out.println(" " + userName + " has not complete or claim the daily loot box.\n");
					} else {
						int index = tempTrx.elementAt(b).indexOf("\"claim_trx_id\":\"");
						int index2 = tempTrx.elementAt(b).lastIndexOf("\"");
						trxID = tempTrx.elementAt(b).substring(index + 16, index2);
						GetTrxDetail(trxID, userName, consolidateReward);
					}
				}
			}
		} else {
			
			System.out.println(" Username : " + userName + " maybe wrong or Splinterland server has problem");
		}
	}

	public static void GetTrxDetail(String trxID, String userName, Vector<RewardLibrary> consolidateReward)
			throws Exception {
		CardDirectory album = new CardDirectory();
		Vector<String> tempString = new Vector<String>();
		URL url = new URL("https://api.splinterlands.io/transactions/lookup?trx_id=" + trxID);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			for (String s : line.split(",")) {
				tempString.add(s);
			}
		}
		bufferedReader.close();
		con.disconnect();		
		System.out.println(" Username : " + userName);
		for (int a = 0; a < tempString.size(); a++) {
			if (tempString.elementAt(a).contains("dec\\\"")) {
				String decString = null;
				int index = tempString.elementAt(a + 1).lastIndexOf("\\\"quantity\\\":");
				int index2 = tempString.elementAt(a + 1).indexOf("}");
				decString = tempString.elementAt(a + 1).substring(index + 13, index2);
				System.out.println(" Dec : " + decString);
				CheckConsolidateReward("dec", Integer.parseInt(decString), consolidateReward);
			}

			if (tempString.elementAt(a).contains("credits")) {
				String creditString = null;
				int index = tempString.elementAt(a + 1).lastIndexOf("\\\"quantity\\\":");
				int index2 = tempString.elementAt(a + 1).indexOf("}");
				creditString = tempString.elementAt(a + 1).substring(index + 13, index2);
				System.out.println(" Credits : " + creditString);
				CheckConsolidateReward("credits", Integer.parseInt(creditString), consolidateReward);
			}

			if (tempString.elementAt(a).contains("reward_card")) {
				String card = null;
				String foil = null;
				String gFoil = null;
				String cardValue = null;
				int index = tempString.elementAt(a + 3).indexOf("\\\"card_detail_id\\\":");
				int index2 = tempString.elementAt(a + 5).indexOf("\\\"gold\\\":");
				cardValue = tempString.elementAt(a + 3).substring(index + 19);
				foil = tempString.elementAt(a + 5).substring(index2 + 9);
				card = album.CardDirectory().get(Integer.parseInt(cardValue));
				if (foil.equalsIgnoreCase("true")) {
					gFoil = "Gold:" + card;
				} else {
					gFoil = card;
				}
				System.out.println(" Card : " + gFoil);
				CheckConsolidateReward(gFoil, 1, consolidateReward);
			}

			if (tempString.elementAt(a).contains("\"potion\\\"")) {
				String potionType = null;
				int index2 = tempString.elementAt(a + 2).indexOf("\\\"potion_type\\\":\\\"");
				int index3 = tempString.elementAt(a + 2).indexOf("\\\"}");
				potionType = tempString.elementAt(a + 2).substring(index2 + 18, index3);
				System.out.println(" Potion : " + potionType);
				CheckConsolidateReward(potionType, 1, consolidateReward);
			}
		}
		System.out.println("\n");
	}

	public static void CheckConsolidateReward(String type, int quantity, Vector<RewardLibrary> consolidateReward) {
		if (consolidateReward.isEmpty()) {
			if (type.equalsIgnoreCase("dec")) {
				RewardLibrary data = new RewardLibrary(type, quantity);
				consolidateReward.add(data);
			} else if (type.equalsIgnoreCase("credits")) {
				RewardLibrary data = new RewardLibrary(type, quantity);
				consolidateReward.add(data);
			} else if (type.equalsIgnoreCase("legendary")) {
				RewardLibrary data = new RewardLibrary(type, quantity);
				consolidateReward.add(data);
			} else if (type.equalsIgnoreCase("gold")) {
				RewardLibrary data = new RewardLibrary(type, quantity);
				consolidateReward.add(data);
			} else {
				RewardLibrary data = new RewardLibrary(type, quantity);
				consolidateReward.add(data);
			}
		} else {
			boolean isExist = false;
			if (type.equalsIgnoreCase("dec")) {
				isExist = CheckTypeExistinElement(type, consolidateReward);
				if (isExist == true) {
					int index = GetIndexFromElement(type, consolidateReward);
					int previousValue = consolidateReward.elementAt(index).getAmount();
					previousValue += quantity;
					consolidateReward.elementAt(index).setAmount(previousValue);
				} else {
					RewardLibrary data = new RewardLibrary(type, quantity);
					consolidateReward.add(data);
				}
			} else if (type.equalsIgnoreCase("credits")) {
				isExist = CheckTypeExistinElement(type, consolidateReward);
				if (isExist == true) {
					int index = GetIndexFromElement(type, consolidateReward);
					int previousValue = consolidateReward.elementAt(index).getAmount();
					previousValue += quantity;
					consolidateReward.elementAt(index).setAmount(previousValue);
				} else {
					RewardLibrary data = new RewardLibrary(type, quantity);
					consolidateReward.add(data);
				}
			} else if (type.equalsIgnoreCase("legendary")) {
				isExist = CheckTypeExistinElement(type, consolidateReward);
				if (isExist == true) {
					int index = GetIndexFromElement(type, consolidateReward);
					int previousValue = consolidateReward.elementAt(index).getAmount();
					previousValue += quantity;
					consolidateReward.elementAt(index).setAmount(previousValue);
				} else {
					RewardLibrary data = new RewardLibrary(type, quantity);
					consolidateReward.add(data);
				}
			} else if (type.equalsIgnoreCase("gold")) {
				isExist = CheckTypeExistinElement(type, consolidateReward);
				if (isExist == true) {
					int index = GetIndexFromElement(type, consolidateReward);
					int previousValue = consolidateReward.elementAt(index).getAmount();
					previousValue += quantity;
					consolidateReward.elementAt(index).setAmount(previousValue);
				} else {
					RewardLibrary data = new RewardLibrary(type, quantity);
					consolidateReward.add(data);
				}
			} else {
				isExist = CheckTypeExistinElement(type, consolidateReward);
				if (isExist == true) {
					for (RewardLibrary element : consolidateReward) {
						if (element.getName() == type) {
							int previousValue = element.getAmount();
							previousValue += quantity;
							element.setAmount(previousValue);
						}
					}
				} else {
					RewardLibrary data = new RewardLibrary(type, quantity);
					consolidateReward.add(data);
				}
			}
		}
	}

	public static boolean CheckTypeExistinElement(String type, Vector<RewardLibrary> consolidateReward) {
		boolean isExist = false;
		for (RewardLibrary element : consolidateReward) {
			if (element.getName().equalsIgnoreCase(type)) {
				isExist = true;
				break;
			}
		}
		return isExist;
	}

	public static int GetIndexFromElement(String type, Vector<RewardLibrary> consolidateReward) {
		int index = 0;
		for (int a = 0; a < consolidateReward.size(); a++) {
			if (consolidateReward.elementAt(a).getName().equalsIgnoreCase(type)) {
				index = a;
				break;
			}
		}
		return index;
	}

	public static Vector<String> ReadUserFromFile() throws Exception {
		Vector<String> user = new Vector<String>();
		int counter = 0;
		Path resourceDirectory = Paths.get("Config", "user.txt");
		File file = new File(resourceDirectory.toUri());
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line;
		while ((line = br.readLine()) != null) {
			user.add(line);
			counter++;
		}
		fr.close();
		totalSize = counter;
		System.out.println(" "+ counter + " Account Loaded");
		return user;
	}

	public static void WelcomeMessage()
	{
		System.out.println("###########################################################");
		System.out.println("#      Welcome to Kurotamashi Daily Reward Statistic      #");
		System.out.println("###########################################################\n");
	}
	
	public static void EndMessage()
	{
		System.out.println("###########################################################");
		System.out.println("# Donate DEC/SPS into the game to the player kurotamashi  #");
		System.out.println("###########################################################");
	}
	
	public static void TotalLoot(Vector<RewardLibrary> consolidateReward)
	{
		System.out.println("\n###########################################################");
		System.out.println("            Total Loot : " + accountRun + " out of " + totalSize + " Accounts           ");
		System.out.println("###########################################################\n");
		for (RewardLibrary a : consolidateReward) {
			System.out.print(" " + a.getName() + " : " + a.getAmount() + "\n");
		}
	}

}
