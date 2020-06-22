package io.project.coronavirustracker.tracker.controller;

import io.project.coronavirustracker.tracker.model.Deaths;
import io.project.coronavirustracker.tracker.model.LocationStats;
import io.project.coronavirustracker.tracker.model.RecoveryCases;
import io.project.coronavirustracker.tracker.service.DataSevice;
//import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class HomeController {

    @Autowired
    DataSevice dataSevice;
//    ChartService chartService;

    @GetMapping("/")
    public String home(Model model){
        List<LocationStats> allStats = dataSevice.getAllStats();
        List<Deaths> deaths = dataSevice.getDeath();
        List<RecoveryCases> recovery = dataSevice.getNewRecovery();

        int totalReportedCases = allStats.stream().mapToInt(state-> state.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
        int totaldeath = deaths.stream().mapToInt(stat->stat.getDeath()).sum();
        int totalrecovered = recovery.stream().mapToInt(stat->stat.getRecovery()).sum();
        int totalactive = totalReportedCases - totaldeath - totalrecovered;


        List<HashSet<String>> countryList = new ArrayList<>();
        HashSet<String> countryset = new HashSet<>();
        for(Deaths contry : deaths){
            countryset.add(contry.getCountry());
        }
        countryList.add(countryset);
        System.out.println("countryset : "+countryset);

        Map<String, String> stateCont = new HashMap<>();
        for(Deaths statcont : deaths){
            stateCont.put(statcont.getState(), statcont.getCountry());
        }
        System.out.println("state-country: "+stateCont);

        HashMap<String, Integer> stateDeath = new HashMap<>();
        for(Deaths statedeat : deaths){
            stateDeath.put(statedeat.getState(), statedeat.getDeath());
        }
        System.out.println("state-death:"+stateDeath );

        Map<String, Integer> contDeath = new HashMap<>();
        for(Deaths contdeat : deaths) {
            contDeath.put(contdeat.getCountry(), contdeat.getDeath());
        }
        System.out.println("country-death: "+contDeath);

        Map<Integer, String> deathCont = new HashMap<>();
        for(Deaths deathcontt : deaths) {
            deathCont.put(deathcontt.getDeath(), deathcontt.getCountry());
        }
        System.out.println("country-death: "+deathCont);

        List<String> states = new ArrayList<>();
        for(Map.Entry<String, String> statecntry : stateCont.entrySet()){
            for(Map.Entry<String, Integer> statedeath : stateDeath.entrySet()){
//                if(cntry.getCountry() == statecntry.getValue()){
                if(statecntry.getKey() == statedeath.getKey())
                    System.out.println(statecntry.getValue()+" "+statecntry.getKey()+" "+statedeath.getValue());

//                } else if(cntry.getCountry() == statecntry.getValue() && statecntry.getKey()==null){
//                    System.out.println(cntry.getCountry()+" "+statecntry.getKey());
//                }
            }
        }
//        List<HashMap<String, Integer>> contDeathList = new ArrayList<>();
//        HashMap<String, Integer> contDeath = new HashMap<>();
//        for(Deaths death: deaths){
//            contDeath.put(death.getCountry(),death.getDeath());
//        }
//        contDeathList.add(contDeath);
//        System.out.println("contDeath : "+contDeath);

        HashMap<String, Integer> updatedContDeath = new HashMap<>();
//        for(String cont : country) {
//            for (Map.Entry<String, Integer> list : contDeath.entrySet()) {
////                System.out.println(cont);
//                int deathInState = 0;
//                if(list.getKey()==cont) {
////                    contState.put(death.getState(), death.getCountry());
//                    System.out.println("in if condition : "+list.getKey()+" "+list.getValue());
//                    deathInState = deathInState + list.getValue();
//                }
//                updatedContDeath.put(cont, deathInState);
//            }
//        }
        model.addAttribute("contDeath",updatedContDeath);
        model.addAttribute("country", countryset);
        model.addAttribute("deaths", deaths);
        model.addAttribute("locationStats", allStats);
        model.addAttribute("recovery", recovery);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);
        model.addAttribute("totalDeaths", totaldeath);
        model.addAttribute("totalrecovered", totalrecovered);
        model.addAttribute("totalActive", totalactive);

        return "home";
    }

//    @GetMapping("/graph.html")
//    public String graph(Model model) throws IOException {
//        JSONArray json = chartService.fetchGraph();
//        model.addAttribute("json",json);
//        return "graph";
//    }
}
