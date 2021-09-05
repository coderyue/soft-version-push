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
public class SoftUpdateService {

    private Logger logger = LoggerFactory.getLogger(SoftUpdateService.class);

    @Autowired
    private PushUtil pushUtil;

    /**
     * push, schedule那里进行调用
     * 软件版本采用正则的方式去获取， 然后匹配现有的版本， 看有没有最新的版本
     */
    public void myPush() throws IOException {
        // 配置文件数据
        List<Map<String, String>> dataMapList = new ArrayList<>();
        // 更新内容
        List<String> toBeSaveList = Collections.synchronizedList(new ArrayList<>());
        // 推送列表
        List<Map<String, String>> pushMapList = Collections.synchronizedList(new ArrayList<>());
        // 超时列表
        List<String> timeOutList = Collections.synchronizedList(new ArrayList<>());
        // 正则未匹配到列表
        List<String> regexNotMatchList = Collections.synchronizedList(new ArrayList<>());
        File file = new File(System.getProperty("user.dir") + File.separator + "soft-regex.txt");
        logger.info("==read file:" + file.getAbsolutePath());
//        File file = new File("F:\\workspace\\myworkspace\\soft-version-push\\src\\main\\resources\\soft-regex.txt");
        if (file.exists()) {
            readDataToMapList(dataMapList, file, toBeSaveList);

            DealDataMapList(dataMapList, pushMapList, timeOutList, regexNotMatchList);

            dealPushData(pushMapList, timeOutList, regexNotMatchList);

            logger.info("=========可更新列表\n" +  pushMapList);
            logger.info("=========请求超时列表\n" +  timeOutList);
            logger.info("=========正则未匹配列表\n" +  regexNotMatchList);
            // 保存最新版本信息到文件
            saveVersionToFile(dataMapList, file, toBeSaveList);
        } else {
            logger.info("===配置文件不存在===");
        }
    }

    /**
     * 处理请求结果并推送
     * @param pushMapList
     * @param timeOutList
     * @param regexNotMatchList
     */
    private void dealPushData(List<Map<String, String>> pushMapList, List<String> timeOutList, List<String> regexNotMatchList) {
        String title = "";
        String content = "";

        String updateInfo = pushMapList.stream().map(item -> "【" + item.get("title") + "】\t" + item.get("content")).collect(Collectors.joining("\n"));
        String timeOutInfo = String.join("\n", timeOutList);
        String notMarchInfo = String.join("\n", regexNotMatchList);

        if (!updateInfo.isEmpty()) {
            title += "可更新 ";
            content += "[可更新]\n" + updateInfo + "\n";
        }
        if (!timeOutInfo.isEmpty()) {
            title += "有超时 ";
            content += "[请求超时]\n" + timeOutInfo + "\n";
        }
        if (!notMarchInfo.isEmpty()) {
            title += "有未匹配";
            content += "[正则未匹配]\n" + notMarchInfo + "\n";
        }

        if (StringUtils.hasText(title)) {
            pushUtil.pushMsg(title, content);
        }

    }

    /**
     * 更新文件中版本信息
     * @param dataMapList
     * @param file
     * @param toBeSaveList
     * @throws IOException
     */
    private void saveVersionToFile(List<Map<String, String>> dataMapList, File file, List<String> toBeSaveList) throws IOException {
        if (dataMapList.isEmpty()) {
            return;
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (String s : toBeSaveList) {
            writer.write(s);
            writer.newLine();
        }

        for (Map<String, String> item : dataMapList) {
            writer.write(item.get("software") + "|" + item.get("regex") + "|" +
                    item.get("url") + "|" + (item.get("curVersion") == null ? "" : item.get("curVersion")));
            writer.newLine();
        }
        writer.close();
    }

    /**
     * 请求并获取更新信息
     * @param dataMapList
     * @param pushMapList
     * @param timeOutList
     * @param regexNotMatchList
     */
    private void DealDataMapList(List<Map<String, String>> dataMapList, List<Map<String, String>> pushMapList, List<String> timeOutList, List<String> regexNotMatchList) {
        dataMapList.stream().parallel().forEach(item -> {
            String software = item.get("software");
            String matchRegex = item.get("regex");
            String url = item.get("url");
            String curVersion = item.get("curVersion");

            HttpResponse<String> response;
            try {
                response = Unirest.get(url).asString();
            } catch (Exception e) {
                logger.info(software + "请求失败=======" + e.getMessage());
                timeOutList.add(software);
                return;
            }
            Pattern regex = Pattern.compile(matchRegex);
            Matcher matcher = regex.matcher(response.getBody());
            if (matcher.find()) {
                String group = matcher.group();
                Map<String, String> tempMap = new HashMap<>();
                if (StringUtils.hasLength(curVersion)) {
                    if (!curVersion.equalsIgnoreCase(group)) {
                        tempMap.put("title", software);
                        item.put("curVersion", group);
                        tempMap.put("content", group);
                        pushMapList.add(tempMap);
                    }
                } else {
                    item.put("curVersion", group);
                    tempMap.put("title", software);
                    tempMap.put("content", group);
                    pushMapList.add(tempMap);
                }
            } else {
                regexNotMatchList.add(software);
            }
        });
    }

    /**
     * 读取文件内容到集合
     * @param dataMapList
     * @param file
     * @param toBeSaveList
     * @throws IOException
     */
    private void readDataToMapList(List<Map<String, String>> dataMapList, File file, List<String> toBeSaveList) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = "";
        while ((line = reader.readLine()) != null && StringUtils.hasText(line)) {
            if (line.startsWith("#")) {
                toBeSaveList.add(line);
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
