package org.stonecipher;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public class StudentApplicationManager extends JavaPlugin {

    private FileConfiguration config = getConfig();

    private static Permission perms = null;
    private ArrayList<Question> questions = new ArrayList<Question>();
    private ArrayList<Applicant> applicants = new ArrayList<Applicant>();
    private ArrayList<Application> applications = new ArrayList<Application>();

    @Override
    public void onEnable() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        loadApplications();
        for (Application application : applications) {
            getLogger().info("Loaded application: " + application.getApplicationName());
        }
        setupPermissions();
        setupQuestions();
        getLogger().info("Loaded StudentApplicationManager");
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player p = (Player) sender;

        if(cmd.getName().equals("apply")) {
            if (isInApplicants(p)) {
                sendPretty(p, "You are currently in the application process.");
                return true;
            } else if (!isVisitor(p)) {
                sendPretty(p, "You are not eligible for the application process.");
                return true;
            }

            addApplicant(p);
            sendPretty(p, "You are now beginning the application process.");
            sendPretty(p, "Answer §7/yes§r to answer yes to a question.");
            sendPretty(p, "Answer §7/no§r to answer no to a question.");
            sendNextQuestion(getApplicant(p));

        } else if(cmd.getName().equals("yes")) {
            if (!isInApplicants(p)) {
                sendPretty(p, "You are currently not in the application process.");
                return true;
            } else if (!isVisitor(p)) {
                sendPretty(p, "You are not eligible for the application process.");
                return true;
            }

            Applicant applicant = getApplicant(p);
            if (applicant.evaluate(Question.Answer.YES, questions.get(applicant.getProgress()))) {
                applicant.incrementProgress();
                if(isFinished(applicant)) {
                    promoteApplicant(applicant);
                } else {
                    sendNextQuestion(applicant);
                }
            } else {
                sendPretty(p, "That was not an expected answer. Restart your application process by executing §7/apply");
                applicants.remove(applicant);
            }


        } else if(cmd.getName().equals("no")) {
            if (!isInApplicants(p)) {
                sendPretty(p, "You are currently not in the application process.");
                return true;
            } else if (!isVisitor(p)) {
                sendPretty(p, "You are not eligible for the application process.");
                return true;
            }

            Applicant applicant = getApplicant(p);
            if (applicant.evaluate(Question.Answer.NO, questions.get(applicant.getProgress()))) {
                applicant.incrementProgress();
                if(isFinished(applicant)) {
                    promoteApplicant(applicant);
                } else {
                    sendNextQuestion(applicant);
                }
            } else {
                sendPretty(p, "That was not an expected answer. Restart your application process by executing §7/apply");
                applicants.remove(applicant);
            }
        }
        return true;
    }
    private void setupConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(), "config.yml");
    }
    private void loadApplications() {
        String basePath = "application";
        for (String appName : getConfig().getConfigurationSection(basePath).getKeys(false)) {
            String presentPath = basePath + "." + appName;
            ConfigurationSection section = getConfig().getConfigurationSection(presentPath);
            Application tmpApplication = new Application(appName, section);
            applications.add(tmpApplication);
        }
    }

    private void promoteApplicant(Applicant applicant) {
        applicants.remove(applicant);
        getServer().dispatchCommand(Bukkit.getConsoleSender(), "lp user " + applicant.getPlayer().getDisplayName() + " parent set student");
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF("school");
        applicant.getPlayer().sendPluginMessage(this, "BungeeCord", out.toByteArray());
        sendPretty(applicant.getPlayer(), "Congratulations! You can claim your plot by executing §7/p auto");
    }

    private boolean isFinished(Applicant applicant) {
        if (applicant.getProgress() == questions.size()) return true;
        return false;
    }

    private boolean isVisitor(Player p) {
        String[] groups = perms.getPlayerGroups(p);
        if(groups[0].equals("Visitor")) return true;
        return false;
    }

    private Applicant getApplicant(Player p) {
        for (Applicant applicant : applicants) {
            if (applicant.getPlayer().equals(p)) return applicant;
        }
        return null;
    }

    private void addApplicant(Player p) {
        applicants.add(new Applicant(p));
    }

    private void sendNextQuestion(Applicant applicant) {
        sendPretty(applicant.getPlayer(), questions.get(applicant.getProgress()).getMessage());
    }

    private boolean isInApplicants(Player p) {
        for (Applicant applicant : applicants) {
            if (applicant.getPlayer().equals(p)) return true;
        }
        return false;
    }

    private void sendPretty(Player p, String message) {
        p.sendMessage("§8[§7Application§8] §r" + message);
    }

    private void setupQuestions() {
        questions.add(new Question(Question.Answer.YES, "Are you interested in learning about computational redstone?"));
        questions.add(new Question(Question.Answer.EITHER, "Do you have previous experience in redstone?"));
        questions.add(new Question(Question.Answer.YES, "Have you read the rules? (https://openredstone.org/rules)"));
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
}
