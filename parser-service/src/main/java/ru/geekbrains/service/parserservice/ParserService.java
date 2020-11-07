package ru.geekbrains.service.parserservice;

import lombok.Data;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.model.Parser;
import ru.geekbrains.model.Task;
import ru.geekbrains.service.requesthandler.TaskService;

import java.util.*;

/**
 * 1. Парсеры регистрируются через метод register().
 * 2. Стартует планировщик задач, который проверяет TaskService на наличие задач, а также мониторит парсеры на предмет завершения парсинга.
 * Если работа всех парсеров завершена, он получает результаты, удаляет задачу из taskService и отправляет инфу в callbackService.
 */
@Data
@Slf4j
@Service
public class ParserService {

    private TaskService taskService;
    private AdService adService;
    private List<Parser> parsers = new LinkedList<>();
    private final Timer timer;
    private Task task;
    private Map<String, Object> ads = new HashMap<>();
    private boolean processing = false;
    private long delay = 2000;

    @Autowired
    public ParserService(TaskService taskService, AdService adService) {
        this.taskService = taskService;
        this.adService = adService;
        timer = new Timer("Checking tasks");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                checkingTasks();
            }
        };
        log.info("Start timer");
        timer.schedule(timerTask, new Date(), delay);
    }

    private void checkingTasks(){

        log.info("checking tasks");

        if (!taskService.isEmpty() && !processing) {
            log.info("start task!");
            parsers.forEach(p -> {
                p.start(taskService.peek().getCountry(), taskService.peek().getCity());
            });
            processing = true;
        }

        if (!taskService.isEmpty() && processing){
            if(parsers.stream().allMatch(Parser::getProcessingStatus)){
                log.info("processing...");
            } else {
                checkResult();
                if(parsers.stream().noneMatch(Parser::getProcessingStatus)){
                    taskService.poll();
                    processing = false;
                }
            }
        }

        if(taskService.isEmpty())
        log.info("taskService.isEmpty()");
    }

    private void checkResult(){
        parsers.forEach(p -> {
            if(!p.getProcessingStatus()){
                log.info("getting results from " + p.getName());
                //получаем список
                adService.saveAds(p.getResult(), p.getName());
            }
        });
    }

    public void register(Parser parser){
        log.info(String.format("parser %s has been registered", parser.getName()));
        parsers.add(parser);
    }
}
