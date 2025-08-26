package me.dark_infect.specialevents.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

public class PermissionController {
    private static PermissionController instance;
    private LuckPerms api;
    private RegisteredServiceProvider<LuckPerms> provider;
    public static PermissionController getInstance(){
        if(instance == null){
            instance = new PermissionController();
        }
        return instance;
    }
    PermissionController(){
        provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if(provider == null){
            throw new IllegalArgumentException("provider is null");
        }
        api = provider.getProvider();
    }
    public void addPermission(User user, String permission) {
        user.data().add(Node.builder(permission).build());
        api.getUserManager().saveUser(user);
    }
    public void removePermission(User user, String permission){
        user.data().remove(Node.builder(permission).build());
        api.getUserManager().saveUser(user);
    }
    public void removePermission(UUID userUuid, String permission){
        api.getUserManager().modifyUser(userUuid, user -> {
            user.data().remove(Node.builder(permission).build());
        });
    }
    public void addPermission(UUID userUuid, String permission) {
        api.getUserManager().modifyUser(userUuid, user -> {
            user.data().add(Node.builder(permission).build());
        });
    }
}
