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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class ScannerController {

    @GetMapping("/scan")
    public List<Integer> scanPorts() {
        List<Integer> openPorts = new ArrayList<>();

        for (int port = 80; port <= 30000; port++) {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("127.0.0.1", port), 50);
                socket.close();
                openPorts.add(port);
                System.out.println("Port " + port + " is open!");
            } catch (Exception e) {}
        }

        return openPorts;
    }

    // LEARN AS YOU BUILD IT.
    @GetMapping("/close/{port}")
    public String closePort(@PathVariable int port) {
        String result = "";
        try {
            /*
            Wat the code doing:

            1. the commands in processbuilder
            2. create a process to run that.
            3. get the output of the process by input pipeline


            4. commands to kill thro processbuilder nd waitfor completion.
            5. return the result.


            */
            ProcessBuilder findPid = new ProcessBuilder(
                "lsof",
                "-t",
                "-i:" + port
            );

            Process findProcess = findPid.start();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(findProcess.getInputStream())
            );

            String pid = reader.readLine();

            if (pid != null && !pid.trim().isEmpty()) {
                pid = pid.trim();
                System.out.println("Found PID: " + pid);

                ProcessBuilder killPid = new ProcessBuilder("kill", "-9", pid);
                int exitCode = killPid.start().waitFor();

                result = (exitCode == 0)
                    ? "Success: Port " + port + " closed."
                    : "Failed to kill process.";
            } else {
                result = "Failed: No process found on port " + port;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = "Error: " + e.getMessage();
        }
        return result;
    }
}
