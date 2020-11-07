package ru.geekbrains.parser.cian;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.entity.Ad;
import ru.geekbrains.entity.Address;
import ru.geekbrains.model.Parser;
import ru.geekbrains.parser.ApartmentParserInterface;
import ru.geekbrains.parser.cian.utils.AdsNotFoundException;
import ru.geekbrains.parser.cian.utils.CaptchaException;
import ru.geekbrains.parser.cian.utils.CianRegionDefiner;
import ru.geekbrains.parser.cian.utils.DataExtractor;
import ru.geekbrains.service.parserservice.ParserService;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;

/**
 * Has methods to parse html from cian.ru
 * <p>
 * Full GET request URL to get Cian ads is: https://www.cian.ru/cat.php?currency=2&deal_type=rent&engine_version=2&maxprice=20000&minprice=10000&offer_type=flat&p=1&region=4581&room1=1&room2=1&room3=1&type=4
 * Where
 * https://www.cian.ru/cat.php - main URL;
 * Get parameters:
 * currency=2 - designation is undefined yet
 * deal_type=rent - type of the deal rent or sale
 * engine_version=2 - designation is undefined yet
 * maxprice=
 * minprice=
 * offer_type=flat - subject of a search may be flat or suburban for house searching
 * p= - page number
 * region= - region number
 * room0= - room search, value 1 if checkbox is marked
 * room1= - 1room flat, value 1 if checkbox is marked
 * room2= - 2room flat, value 1 if checkbox is marked
 * room3= - 4room flat, value 1 if checkbox is marked
 * type=4 - has a impact on results (change some link which logic related to
 * </p>
 */

@Slf4j
@Component
public class CianParser extends Parser {

    private final DataExtractor dataExtractor;
    private final CianRegionDefiner cianRegionDefiner;
    private final List<ApartmentParserInterface> cianApartments = new ArrayList<>();
    private boolean isProcessing = false;
    private final String name = "Циан";
    private ParserService parserService;

    @Autowired
    public CianParser(DataExtractor dataExtractor, CianRegionDefiner cianRegionDefiner, ParserService parserService) {
        this.dataExtractor = dataExtractor;
        this.cianRegionDefiner = cianRegionDefiner;
        this.parserService = parserService;

        this.parserService.register(this);
    }

    /**
     * Fills Map interface implementor with <code>Address</code> as a key and List<code></><Ad></code> as a value
     *
     * @param country Name of a country where parser should find adTags
     * @param city    Name of a city in which parser should find adTags
     */
    public void start(String country, String city) {
        isProcessing = true;
        if (!country.equals("Россия")) {
            throw new RuntimeException("You are searching adTags in the country " + country + " but we can find ads only in 'Россия'");
        }

        List<String> regionCodes = cianRegionDefiner.getRegions(city);
        String pageValue = "1";
        boolean hasNextPage = true;
        for (String regionCode : regionCodes) {
            while (!pageValue.equals("3")) { // uncomment to limit search deep to 2 pages. Needed to prevent blocking by IP
//            while (hasNextPage) {  // uncomment to search throughout all the target pages

                URIBuilder uri = new URIBuilder();
                uri.setScheme("https")
                        .setHost("www.cian.ru")
                        .setPath("/cat.php")
                        .addParameter("engine_version", "2")
                        .addParameter("deal_type", "rent")
                        .addParameter("offer_type", "flat")
                        .addParameter("region", regionCode)
                        .addParameter("p", pageValue);

                WebDriver driver = new HtmlUnitDriver();
                driver.get(uri.toString());
                Document document = Jsoup.parse(driver.getPageSource());

                Elements adTags = document.getElementsByTag("article");
                if (document.title().contains("Captcha"))
                    throw new CaptchaException("Your IP address has been blocked");
                if (adTags.size() == 0)
                    throw new AdsNotFoundException("There are no ads can be parsed from the page. May be Cian had changed html structure");

                log.info("Amount of tags on the page: " + adTags.size());

                for (Element adTag : adTags) {
                    CianApartment cianApartment = dataExtractor.buildApartment(adTag);
                    if (cianApartment.getCity().equals(city)) cianApartments.add(cianApartment);
                }
                String pageValueMark = document.selectFirst("div[data-name~=^Pagination]").getElementsByTag("li").last().children().first().text();
                if (!pageValueMark.equals("..")) {
                    hasNextPage = false;
                }

                pageValue = String.valueOf(Integer.parseInt(pageValue) + 1);

            }
        }
        isProcessing = false;
    }


    public List<ApartmentParserInterface> getResult(String city) {

        return cianApartments;
    }

    public boolean getProcessingStatus() {
        return isProcessing;
    }

    @Override
    public String getName() {
        return name;
    }


}

