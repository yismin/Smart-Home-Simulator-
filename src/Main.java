import Controller.CentralController;
import Devices.*;
import Structure.Home;
import Structure.Room;
import Automation.AutomationEngine;
import Automation.AutomationRule;
import exceptions.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("===================================");
        System.out.println("  SMART HOME AUTOMATION SIMULATOR  ");
        System.out.println("===================================\n");

        // Create the home
        Home myHome = new Home("My Smart Home");

        // Create rooms
        Room livingRoom = new Room("Living Room");
        Room bedroom = new Room("Bedroom");
        Room kitchen = new Room("Kitchen");

        // Create devices
        Light livingLight = new Light("L001", "Living Room Light", 75);
        Light bedroomLight = new Light("L002", "Bedroom Light", 50);
        Thermostat livingThermostat = new Thermostat("T001", "Main Thermostat", 22);
        SmartTV tv = new SmartTV("TV001", "Living Room TV");
        MotionSensor motionSensor = new MotionSensor("S001", "Living Room Sensor");
        Light kitchenLight = new Light("L003", "Kitchen Light", 100);

        // Add devices to rooms
        try {
            livingRoom.addDevice(livingLight);
            livingRoom.addDevice(livingThermostat);
            livingRoom.addDevice(tv);
            livingRoom.addDevice(motionSensor);

            bedroom.addDevice(bedroomLight);

            kitchen.addDevice(kitchenLight);

            // Add rooms to home
            myHome.addRoom(livingRoom);
            myHome.addRoom(bedroom);
            myHome.addRoom(kitchen);

        } catch (DuplicateDeviceException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Create central controller
        CentralController controller = new CentralController(myHome);

        // Create automation engine
        AutomationEngine automationEngine = new AutomationEngine();

        // Add automation rules
        AutomationRule motionRule = new AutomationRule(
                "Motion Light Rule",
                () -> motionSensor.isMotionDetected(),
                () -> {
                    if (!livingLight.isOn) {
                        livingLight.turnOn();
                        System.out.println("[AUTOMATION] Motion detected! Living room light turned on.");
                    }
                }
        );

        AutomationRule energySavingRule = new AutomationRule(
                "Energy Saving Rule",
                () -> controller.getTotalEnergyConsumption() > 200,
                () -> {
                    System.out.println("[AUTOMATION] High energy consumption detected! Reducing usage...");
                    livingLight.setBrightness(30);
                    tv.turnOff();
                }
        );

        automationEngine.addRule(motionRule);
        automationEngine.addRule(energySavingRule);

        // Demo menu
        runDemo(controller, automationEngine, motionSensor, myHome);
    }

    private static void runDemo(CentralController controller, AutomationEngine engine,
                                MotionSensor sensor, Home home) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n========== SMART HOME MENU ==========");
            System.out.println("1. Show all devices status");
            System.out.println("2. Turn on all lights");
            System.out.println("3. Turn off all devices");
            System.out.println("4. Show energy consumption");
            System.out.println("5. Control specific device");
            System.out.println("6. Simulate motion detection");
            System.out.println("7. Run automation rules");
            System.out.println("8. Search device by ID");
            System.out.println("9. Exit");
            System.out.print("Choose option: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        controller.showAllDevicesStatus();
                        break;
                    case 2:
                        controller.turnOnAllLights();
                        break;
                    case 3:
                        controller.turnOffAllDevices();
                        break;
                    case 4:
                        System.out.println("\n=== ENERGY REPORT ===");
                        System.out.printf("Total Energy Consumption: %.2f watts\n",
                                controller.getTotalEnergyConsumption());
                        break;
                    case 5:
                        controlSpecificDevice(controller, scanner, home);
                        break;
                    case 6:
                        System.out.println("\n[SIMULATION] Motion detected in living room!");
                        sensor.detectMotion();
                        engine.evaluateRules();
                        break;
                    case 7:
                        System.out.println("\n[AUTOMATION] Evaluating all rules...");
                        engine.evaluateRules();
                        break;
                    case 8:
                        System.out.print("Enter device ID: ");
                        String id = scanner.nextLine();
                        controller.searchDeviceById(id);
                        break;
                    case 9:
                        System.out.println("\nShutting down Smart Home System...");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                scanner.nextLine(); // clear buffer
            }
        }

        scanner.close();
    }

    private static void controlSpecificDevice(CentralController controller,
                                              Scanner scanner, Home home) {
        System.out.print("Enter device ID: ");
        String deviceId = scanner.nextLine();

        try {
            SmartDevice device = controller.findDevice(deviceId);

            if (device instanceof Light) {
                Light light = (Light) device;
                System.out.println("\n1. Turn On");
                System.out.println("2. Turn Off");
                System.out.println("3. Set Brightness");
                System.out.print("Choose: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        light.turnOn();
                        break;
                    case 2:
                        light.turnOff();
                        break;
                    case 3:
                        System.out.print("Enter brightness (0-100): ");
                        int brightness = scanner.nextInt();
                        light.setBrightness(brightness);
                        break;
                }
            } else if (device instanceof Thermostat) {
                Thermostat thermostat = (Thermostat) device;
                System.out.println("\n1. Turn On");
                System.out.println("2. Turn Off");
                System.out.println("3. Set Temperature");
                System.out.print("Choose: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        thermostat.turnOn();
                        break;
                    case 2:
                        thermostat.turnOff();
                        break;
                    case 3:
                        System.out.print("Enter temperature: ");
                        int temp = scanner.nextInt();
                        thermostat.setTemperature(temp);
                        break;
                }
            } else if (device instanceof SmartTV) {
                SmartTV tv = (SmartTV) device;
                System.out.println("\n1. Turn On");
                System.out.println("2. Turn Off");
                System.out.println("3. Change Channel");
                System.out.print("Choose: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        tv.turnOn();
                        break;
                    case 2:
                        tv.turnOff();
                        break;
                    case 3:
                        System.out.print("Enter channel: ");
                        int channel = scanner.nextInt();
                        tv.changeChannel(channel);
                        break;
                }
            }

        } catch (DeviceNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}