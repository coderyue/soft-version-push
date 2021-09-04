package com.lin.service;

import com.lin.util.PushUtil;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 具体推送逻辑d
 */
@Service
public class PushService {

    private Logger logger = LoggerFactory.getLogger(PushService.class);

    @Autowired
    private PushUtil pushUtil;

    /**
     * push, schedule那里进行调用
     * 软件版本采用正则的方式去获取， 然后匹配现有的版本， 看有没有最新的版本
     */
    public void myPush() throws IOException {
        List<Map<String, String>> dataMapList = new ArrayList<>();
        List<Map<String, String>> pushMapList = Collections.synchronizedList(new ArrayList<>());
        File file = new File(System.getProperty("user.dir") + File.separator + "soft-regex.txt");
        logger.info("==read file:" + file.getAbsolutePath());
//        File file = new File("F:\\workspace\\myworkspace\\soft-version-push\\src\\main\\resources\\soft-regex.txt");
        if (file.exists()) {
            readDataToMapList(dataMapList, file);

            getPushDataMapList(dataMapList, pushMapList);

            logger.info("=========版本信息列表\n" +  pushMapList);

            String title = "有软件更新";
            String content = pushMapList.stream().map(item -> "【" + item.get("title") + "】\t" + item.get("content")).collect(Collectors.joining("\n"));
            pushUtil.pushMsg(title, content);
            // 保存最新版本信息到文件
            saveVersionToFile(dataMapList, file);
        } else {
            logger.info("===配置文件不存在===");
        }
    }

    private void saveVersionToFile(List<Map<String, String>> dataMapList, File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("# 软件名称  正则  url     当前版本    中间用|分隔");
        writer.newLine();
        dataMapList.forEach(item -> {
            try {
                writer.write(item.get("software") + "|" + item.get("regex") + "|" +
                        item.get("url") + "|" + (item.get("curVersion") == null ? "" : item.get("curVersion")));
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.close();
    }

    private void getPushDataMapList(List<Map<String, String>> dataMapList, List<Map<String, String>> pushMapList) {
        dataMapList.stream().parallel().forEach(item -> {
            HttpResponse<String> response = Unirest.get(item.get("url")).asString();
            Pattern regex = Pattern.compile(item.get("regex"));
            Matcher matcher = regex.matcher(response.getBody());
            if (matcher.find()) {
                String group = matcher.group();
                Map<String, String> tempMap = new HashMap<>();
                if (StringUtils.hasLength(item.get("curVersion"))) {
                    if (!item.get("curVersion").equalsIgnoreCase(group)) {
                        tempMap.put("title", item.get("software"));
                        item.put("curVersion", group);
                        tempMap.put("content", group);
                        pushMapList.add(tempMap);
                    }
                } else {
                    item.put("curVersion", group);
                    tempMap.put("title", item.get("software"));
                    tempMap.put("content", group);
                    pushMapList.add(tempMap);
                }
            }
        });
    }

    /**
     * 读取文件内容到集合
     * @param dataMapList
     * @param file
     * @throws IOException
     */
    private void readDataToMapList(List<Map<String, String>> dataMapList, File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = "";
        while ((line = reader.readLine()) != null && StringUtils.hasText(line)) {
            if (line.startsWith("#")) {
                continue;
            }
            // 软件名，正则， url， 当前版本
            String[] split = line.split("\\|", -1);
            Map<String, String> tempMap = new HashMap<>();
            tempMap.put("software", split[0]);
            tempMap.put("regex", split[1]);
            tempMap.put("url", split[2]);
            tempMap.put("curVersion", split[3]);
            dataMapList.add(tempMap);
        }
        reader.close();
    }

}
