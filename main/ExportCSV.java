package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import com.opencsv.CSVWriter;

import main.RewardLibrary;

public class ExportCSV {

	public void ExportCSV(Vector<RewardLibrary> rewardDetails) throws Exception {
		String fileName = "/SplinterLandRewardStatistic_" + getTimeStamp() + ".csv";
		Path resourceDirectory = Paths.get("CSV", fileName);
		File file = new File(resourceDirectory.toUri());
		file.createNewFile();
		CSVWriter writer = new CSVWriter(new FileWriter(file.getAbsolutePath()));
		String[] header = setHeader(rewardDetails);
		String[] content = setContent(rewardDetails);
		List list = new ArrayList();
		list.add(header);
		list.add(content);
		writer.writeAll(list);
		writer.flush();
		System.out.println("\n###########################################################");
		System.out.println(" CSV file has been exported to CSV folder");
	}

	public String[] setHeader(Vector<RewardLibrary> rewardDetails) {
		String[] tempHeader = new String[rewardDetails.size()];
		int counter = 0;
		for (RewardLibrary element : rewardDetails) {
			tempHeader[counter] = element.getName();
			counter++;
		}
		return tempHeader;
	}

	public String[] setContent(Vector<RewardLibrary> rewardDetails) {
		String[] tempContent = new String[rewardDetails.size()];
		int count = 0;
		for (RewardLibrary element : rewardDetails) {
			tempContent[count] = String.valueOf(element.getAmount());
			count++;
		}

		return tempContent;
	}

	public static String getTimeStamp() {
		SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
		Date date = new Date();
		return (formatter.format(date)).toString();
	}
}
