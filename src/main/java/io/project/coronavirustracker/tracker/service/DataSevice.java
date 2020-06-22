package io.project.coronavirustracker.tracker.service;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.dataformat.csv.*;
import io.project.coronavirustracker.tracker.model.Deaths;
import io.project.coronavirustracker.tracker.model.LocationStats;
import io.project.coronavirustracker.tracker.model.RecoveryCases;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
//import org.json.JSONArray;
//import org.json.JSONObject;
import org.springframework.beans.factory.parsing.PassThroughSourceExtractor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DataSevice {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static String VIRUS_DATA_URL2 = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
    private static String VIRUS_DATA_URL3 = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";

    private int totalDeath = 0;

    private List<LocationStats> allStats = new ArrayList<>();
    public List<LocationStats> getAllStats() {
        return allStats;
    }

    private List<Deaths> newdeaths = new ArrayList<>();
    public List<Deaths> getDeath() { return newdeaths; }
    
    private List<RecoveryCases> newRecovery = new ArrayList<>();
    public List<RecoveryCases> getNewRecovery() { return newRecovery;}


//1
    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fethchData() throws IOException, InterruptedException {

            List<LocationStats> newStats = new ArrayList<>();
            List<Deaths> death = new ArrayList<>();
            List<RecoveryCases> recoveryCases = new ArrayList<>();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VIRUS_DATA_URL))
                    .build();
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            HttpClient httpClient2 = HttpClient.newHttpClient();
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(VIRUS_DATA_URL2))
                    .build();
            HttpResponse<String> httpResponse2 = httpClient2.send(request2, HttpResponse.BodyHandlers.ofString());

            HttpClient httpClient3 = HttpClient.newHttpClient();
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create(VIRUS_DATA_URL3))
                    .build();
            HttpResponse<String> httpResponse3 = httpClient3.send(request3, HttpResponse.BodyHandlers.ofString());




//            InputStream is = new URL("https://pomber.github.io/covid19/timeseries.json").openStream();
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//            StringBuilder sb = new StringBuilder();
//            int cp;
//            while ((cp = rd.read()) != -1) {
//                sb.append((char) cp);
//            }
//            String jsonText = sb.toString();
//            JSONArray json = new JSONArray(jsonText);
//            System.out.println("json: "+json);

//        JSONObject root = new JSONObject(result);
//        JSONArray plants = root.getJSONArray("plants");
//            CsvSchema csv = CsvSchema.emptySchema().withHeader();
//            CsvMapper csvMapper = new CsvMapper();
//            MappingIterator<Map<?, ?>> mappingIterator =  csvMapper.reader().forType(Map.class).with(csv).readValues(httpResponse.body());
//            List<Map<?, ?>> list = mappingIterator.readAll();
//            System.out.println("csv to json "+list);


            StringReader csvBodyReader = new StringReader(httpResponse.body());
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
            for (CSVRecord record : records) {
                LocationStats locationStats = new LocationStats();
                locationStats.setState(record.get("Province/State"));
                locationStats.setCountry(record.get("Country/Region"));
                int latestCases = Integer.parseInt(record.get(record.size() - 1));
                int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
                locationStats.setLatestTotalCases(latestCases);
                locationStats.setDiffFromPrevDay(latestCases - prevDayCases);
//                System.out.println(locationStats);
                newStats.add(locationStats);

            }
            this.allStats = newStats;

            StringReader csvBodyReader2 = new StringReader(httpResponse2.body());
            Iterable<CSVRecord> records2 = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader2);
            for (CSVRecord record2 : records2) {
                Deaths deat = new Deaths();
                deat.setState(record2.get("Province/State"));
                deat.setCountry(record2.get("Country/Region"));
                totalDeath = Integer.parseInt(record2.get(record2.size() - 1));
                deat.setDeath(totalDeath);
//                System.out.println(" Deaths "+deat);

                death.add(deat);
            }
            this.newdeaths = death;

            StringReader csvBodyReader3 = new StringReader(httpResponse3.body());
            Iterable<CSVRecord> records3 = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader3);
            for (CSVRecord record3 : records3) {
                RecoveryCases recovery = new RecoveryCases();
                recovery.setState(record3.get("Province/State"));
                recovery.setCountry(record3.get("Country/Region"));
//                locationStats3.setTotalDeaths();
                int totalrecovered = Integer.parseInt(record3.get(record3.size() - 1));
                recovery.setRecovery(totalrecovered);
//                System.out.println(" recovered "+recovery);
                recoveryCases.add(recovery);
            }
//            System.out.println(" recovery cases "+recoveryCases);
            this.newRecovery = recoveryCases;


    }

    static String[] headers(String path) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.readLine().split(",");
        }
    }


}
