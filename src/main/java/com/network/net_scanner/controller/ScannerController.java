package com.network.net_scanner.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class ScannerController {

    @GetMapping("/scan")
    public List<Integer> scanPorts(){
        List <Integer> openPorts = new ArrayList<>();

        for (int port = 80; port <= 30000; port++){
            try {
                
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("127.0.0.1", port), 50);
                socket.close();
                openPorts.add(port);
                System.out.println("Port " + port + " is open!");
        

            } catch (Exception e){

            }
        }

        return openPorts;
    }
    @GetMapping("/close/{port}")
    public String closePort(@PathVariable int port) {
        if (port == 8080) return "Self-termination blocked!";

        try {
            ProcessBuilder killBuilder = new ProcessBuilder("fuser", "-k", port + "/tcp");
            Process process = killBuilder.start();

            // 1. Read the error message from Linux
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorMessage = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorMessage.append(line);
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return "Success: Port " + port + " closed.";
            } else {
                // 2. Return the actual Linux error to the browser
                return "Failed! Linux says: " + errorMessage.toString() + " (Exit Code: " + exitCode + ")";
            }
        } catch (Exception e) {
            return "Java Error: " + e.getMessage();
        }
    }
}

    
