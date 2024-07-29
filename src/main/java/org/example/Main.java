package org.example;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static String sptSwitchFolderName = "_SPTSwitch";
    private static String clientModsPath, serverModsPath;
    public static void main(String[] args) {
        String sptPath = args[0];
        clientModsPath = sptPath+"/user/mods";
        serverModsPath = sptPath+"/BepInEx/plugins";
        File folderClientMods = new File(clientModsPath);
        File folderServerMods = new File(serverModsPath);
        createSPTSwitchFolder(sptPath);

        Scanner userInput = new Scanner(System.in);
        if(!hasFile(folderServerMods, "fika").isEmpty() && !hasFile(folderClientMods, "fika").isEmpty()){
            System.out.println("Fika Enabled");
            System.out.println("¿Do you want to disable Fika? (y/n)");
            String s = userInput.nextLine().strip().toLowerCase();
            if(s.equalsIgnoreCase("y")){
                unloadFika(sptPath);
            }
        } else if(!hasFile(folderServerMods, "fika").isEmpty() || !hasFile(folderClientMods, "fika").isEmpty()){
            System.out.println("Only One Fika File Found!");
        } else {
            System.out.println("Fika Disabled");
            System.out.println("¿Do you want to enable Fika? (y/n)");
            String s = userInput.nextLine().strip().toLowerCase();
            if(s.equalsIgnoreCase("y")){
                loadFika(sptPath);
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static ArrayList<File> hasFile(File dirToCheck, String fileToFind){
        ArrayList<File> filesFound = new ArrayList<>();
        for(File f : dirToCheck.listFiles()){
            if(f.getName().toLowerCase().contains(fileToFind)){
                filesFound.add(f);
            }
        }
        return filesFound;
    }
    public static void createSPTSwitchFolder(String sptPath){
        File sptSwitchFolder = new File(sptPath+"/"+sptSwitchFolderName);
        if(!sptSwitchFolder.exists()){
            sptSwitchFolder.mkdir();
        }
    }
    public static void unloadFika(String sptPath){
        File fikaClient = hasFile(new File(clientModsPath),"fika").getFirst();
        File fikaServer = hasFile(new File(serverModsPath),"fika").getFirst();

        File movedFikaClient = new File(sptPath+"/"+sptSwitchFolderName+"/"+fikaClient.getName());
        File movedFikaServer = new File(sptPath+"/"+sptSwitchFolderName+"/"+fikaServer.getName());

        try {
            FileUtils.copyDirectory(fikaClient, movedFikaClient);
            Files.write(movedFikaServer.toPath(),Files.readAllBytes(fikaServer.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            FileUtils.deleteDirectory(fikaClient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fikaServer.delete();

        System.out.println("Fika Disabled Successfully!");
    }
    public static void loadFika(String sptPath){
        File fikaClient = hasFile(new File(sptPath+"/"+sptSwitchFolderName),"fika").getFirst();
        File fikaServer = hasFile(new File(sptPath+"/"+sptSwitchFolderName),"fika").getLast();

        File movedFikaClient = new File(clientModsPath+"/"+fikaClient.getName());
        File movedFikaServer = new File(serverModsPath+"/"+fikaServer.getName());

        try {
            FileUtils.copyDirectory(fikaClient, movedFikaClient);
            Files.write(movedFikaServer.toPath(),Files.readAllBytes(fikaServer.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            FileUtils.deleteDirectory(fikaClient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fikaServer.delete();

        System.out.println("Fika Enabled Successfully");
    }
}