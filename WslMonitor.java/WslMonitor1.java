mport java.io.*;
import java.util.*;
public class WslMonitor{
//global vars to be reminded of last loop # 
static long oldTotalCpu = 0;
static long oldIdleCpu = 0;
static long oldRx = 0;
static long oldTx = 0;
public static voil main(String[] args) throws Exception{
//the exception is a backup  just in case.
while(true){
//I found this on stak Overflow to clear the screen
System.out.print("\033[H\033[2J");

System.out.println("---------------------------------------");
System.out.println("   V1.0 of Linux monitor/task viewer   ");
System.out.println("---------------------------------------");
getCpu();
getRam();
getTemp();
getNet();
getWifi();
getUptime();
getTopProcs();
System.out.println("----------------------------------------");
System.out.println("Press Ctrl+C to end the code");

//wait 2 sec
Thread.sleep(2000);
}
}
public static void getCpu(){
try{
Scanner scan = new Scanner(new File("/proc/stat"));
String firstLine = scan.nextLine();
scan.close();
String[] parts = firstLine.split(" +"); //regex to split by spaces
//grab all Cpu stats from array
long user = Long.parseLong(parts[1]);
long nice = Long.parseLong(parts[2]);
long sys = Long.parseLong(parts[3]);
long idle = Long.parseLong(parts[4]);
long iowait = Long.parseLong(parts[5]);
long irq = Long.parseLong(parts[6]);
long softirq = Long.parseLong(parts[7]);

long totalIdle = idle +iowait;
long totalUsage = user + nice + sys + irq +softirq;
long total = totalIdle + totalUsage;
//cant divie by zero on first run
if (oldTotalCpu != 0;){
long diffTotal = total - oldTotalCpu;
long diffIdle = totalIdle - oldIdleCpu;
long usage = (diffTotal - diffIdle) *100 / diffTotal;
System.out.println("CPU Usage: " + usage + "%");
}
else{
System.out.println("CPU Usage: calc...");
}
oldIdleCpu = totalIdle;
oldTotalCpu = total;
}
catch(Exception e){
System.out.println("CPU error: " + e.getMessage());
}
}
public static void get Ram(){
try{
Scanner scan = new Scanner(new File("/proc/meminfo"));
long memTotal = 0;
long memAvail = 0;

while (scan.hasNextLine()){
String line = scan.nextLine();
if (line.contains("MemTotal:")){
//Stripped the kB text to just get the Numbers
memTotal = Long.paraseLong(line.replaceAll("[^0-9]",""));
}
if (line.contains("MemAvailable:")){
memAvil = Long.parseLong(line.replaceAll("[^0-9]", ""));
}
}
scan.close();

memTotal = Long.parseLong(line.replaceAll("[^0-9]", ""));
}
if(line.contains("MemAvailable:")){
memAvail = Long.parseLong(line.replaceAll("[^0-9]", ""));
}
}
scan.close();

long used = memTotal - memAvail;
long percent = (used * 100) / memTotal;
System.out.println("RAM Usage: " + (used /1024) + "MB / " + (memTotal / 1024) + "MB (" + percent + "%)");
}
catch(Exception e){
System.out.println("RAM error");
}
}


public static void getTemp(){
try {
File tempFile = new File("/sys/class/thermal/thermal_zone0/temp");
if (tempFile.exists()) {
Scanner s = new Scanner(tempFile);
double temp = Double.parseDouble(s.nextLine()) / 1000.0;
s.close();
// formatting is annoying so just rounding it
System.out.println("CPU Temp: " + Math.round(temp * 10.0) / 10.0 + " C");
}
else {
System.out.println("CPU Temp: [Windows blocks this]");
}
}
catch (Exception e) {

}
}

public static void getNet() {
try {
Scanner s = new Scanner(new File("/proc/net/dev"));
while(s.hasNextLine()) {
String line = s.nextLine();
if (line.contains("eth0:")) {
String[] stuff = line.split(":")[1].trim().split(" +");
long rx = Long.parseLong(stuff[0]);
long tx = Long.parseLong(stuff[8]);

if (oldRx != 0) {
// divide by 2 seconds then by 1024 for KB
long rxSpeed = (rx - oldRx) / 2048; 
long txSpeed = (tx - oldTx) / 2048;
System.out.println("Network: DL " + rxSpeed + " KB/s | UL " + txSpeed + " KB/s");
}
else {
System.out.println("Network: calc...");
}

oldRx = rx;
oldTx = tx;
break; // stop reading file
}
}
s.close();
}
catch (Exception e) {
System.out.println("Net error");
}
}
public static void get Wifi(){
//running windows exe from java in Linux is pritty cool
try{
Process p = RunTime.getRuntime().exec(new String[]{"bash", "-c", "netsh.exe wlan show interfaces"});
BufferedReader br = new BufferedReader(newInputStreamReader(p.getInputStream()));
String line;
boolean found = false;

while ((line = br.readLine()) !+ null){
if (line.toLowerCase().contains("signal")){
System.out.println("Host Wi-Fi: " + line.split(":")[1].trim());
found = true;
break;
}
}
if(!found){
System.out.println("Host Wi-Fi: Not connected / on Ethernet");
}
}
catch(Exception e){
System.out.println("Wifi error");
}
}

public static void getUptime() {
try {
Scanner s = new Scanner(new File("/proc/uptime"));
double uptime = s.nextDouble();
s.close();

int hours = (int) (uptime / 3600);
int mins = (int) ((uptime % 3600) / 60);
System.out.println("Uptime: " + hours + " hrs, " + mins + " mins");
}
catch (Exception e) {
System.out.println("Uptime error");
}
}

public static void getTopProcs() {
System.out.println("\n--- Top 5 Processes ---");
try {
// ProcessHandle api is confusing, just calling the ps command is way easier
Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", "ps -eo pid,comm,user --sort=-pcpu | head -n 6"});
BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
String line;
while ((line = br.readLine()) != null) {
System.out.println(line);
}
catch (Exception e) {
System.out.println("Error getting processes");
}
}
}

