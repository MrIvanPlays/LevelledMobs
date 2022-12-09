package me.lokka30.levelledmobs.nms;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class Definitions {
    public Definitions(){
        this.ver = new ServerVersionInfo();
        build();
    }

    private final ServerVersionInfo ver;
    private boolean hasKiori;
    private boolean isOneNinteenThreeOrNewer;

    // classes:
    Class<?> clazz_IChatMutableComponent;
    Class<?> clazz_IChatBaseComponent;
    Class<?> clazz_TranslatableComponent;
    Class<?> clazz_CraftLivingEntity;
    Class<?> clazz_CraftEntity;
    Class<?> clazz_Entity;
    Class<?> clazz_DataWatcher;
    Class<?> clazz_DataWatcher_Item;
    Class<?> clazz_DataWatcherRegistry;
    Class<?> clazz_DataWatcherObject;
    Class<?> clazz_DataWatcherSerializer;
    Class<?> clazz_ClientboundSetEntityDataPacket;
    Class<?> clazz_CraftPlayer;
    Class<?> clazz_Packet;
    Class<?> clazz_PlayerConnection;
    Class<?> clazz_ServerPlayerConnection;
    Class<?> clazz_NetworkManager;
    Class<?> clazz_EntityPlayer;
    Class<?> clazz_PaperAdventure;
    Class<?> clazz_EntityTypes;

    // methods:
    Method method_ComponentAppend;
    Method method_EmptyComponent;
    Method method_TextComponent;
    Method method_Translatable;
    Method method_TranslatableWithArgs;
    Method method_getHandle;
    Method method_getEntityData;
    Method method_set;
    Method method_getId;
    Method method_PlayergetHandle;
    Method method_Send;
    Method method_getAll;
    Method method_define;
    Method method_getAccessor;
    Method method_getValue;
    Method method_AsVanilla;
    Method method_EntityTypeByString;
    Method method_GetDescriptionId;

    // fields
    Field field_OPTIONAL_COMPONENT;
    Field field_BOOLEAN;
    Field field_Connection;

    // Constructors
    Constructor<?> ctor_EntityDataAccessor;
    Constructor<?> ctor_SynchedEntityData;
    Constructor<?> ctor_Packet;

    private void build(){
        try {
            buildClasses();

            // build methods
            getMethodComponentAppend();
            getMethodTextComponents();
            getMethodTranslatable();
            buildSimpleMethods();
            buildFields();
            buildConstructors();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildClasses() throws ClassNotFoundException {
        this.clazz_IChatMutableComponent = Class.forName(
                "net.minecraft.network.chat.IChatMutableComponent");

        this.clazz_IChatBaseComponent = Class.forName(
                "net.minecraft.network.chat.IChatBaseComponent");

        this.clazz_CraftEntity = Class.forName(
                "org.bukkit.craftbukkit." + ver.getNMSVersion() + ".entity.CraftEntity");

        this.clazz_CraftLivingEntity = Class.forName(
                "org.bukkit.craftbukkit." + ver.getNMSVersion() + ".entity.CraftLivingEntity");

        // net.minecraft.network.syncher.SynchedEntityData
        this.clazz_DataWatcher = Class.forName(
                "net.minecraft.network.syncher.DataWatcher");

        this.clazz_DataWatcher_Item = Class.forName(
                "net.minecraft.network.syncher.DataWatcher$Item");

        this.clazz_DataWatcherRegistry = Class.forName(
                "net.minecraft.network.syncher.DataWatcherRegistry");

        this.clazz_Entity = Class.forName(
                "net.minecraft.world.entity.Entity");

        this.clazz_DataWatcherObject = Class.forName(
                "net.minecraft.network.syncher.DataWatcherObject");

        this.clazz_DataWatcherSerializer = Class.forName(
                "net.minecraft.network.syncher.DataWatcherSerializer");

        this.clazz_ClientboundSetEntityDataPacket = Class.forName(
                "net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata");

        this.clazz_CraftPlayer = Class.forName(
                "org.bukkit.craftbukkit." + ver.getNMSVersion() + ".entity.CraftPlayer");

        this.clazz_Packet = Class.forName(
                "net.minecraft.network.protocol.Packet");

        this.clazz_NetworkManager = Class.forName(
                "net.minecraft.network.NetworkManager");

        //net.minecraft.server.network.ServerPlayerConnection ->
        // void send(net.minecraft.network.protocol.Packet) ->
        this.clazz_PlayerConnection = Class.forName(
                "net.minecraft.server.network.PlayerConnection");

        this.clazz_ServerPlayerConnection = Class.forName(
                "net.minecraft.server.network.ServerPlayerConnection");

        // net.minecraft.server.level.ServerPlayer ->
        this.clazz_EntityPlayer = Class.forName(
                "net.minecraft.server.level.EntityPlayer");

        if (ver.getMinecraftVersion() < 1.19) {
            // this is basically TranslatableComponent
            this.clazz_TranslatableComponent = Class.forName(
                    "net.minecraft.network.chat.ChatMessage");
        }

        try {
            this.clazz_PaperAdventure = Class.forName(
                    "io.papermc.paper.adventure.PaperAdventure");
            this.hasKiori = true;
        }
        catch (ClassNotFoundException ignored){ }

        this.clazz_EntityTypes = Class.forName(
                "net.minecraft.world.entity.EntityTypes");
    }

    private void getMethodComponentAppend() throws NoSuchMethodException {
        // net.minecraft.network.chat.MutableComponent append(net.minecraft.network.chat.Component) ->
        // 1.18 = b, 1.19.0 = a, 1.19.1 = b
        final String methodName = ver.getRevision() == 0 || ver.getMinecraftVersion() == 1.18
                ? "a" : "b";

        this.method_ComponentAppend = clazz_IChatMutableComponent.getDeclaredMethod(
                methodName, this.clazz_IChatBaseComponent);
    }

    private void getMethodTextComponents() throws NoSuchMethodException{
        // net.minecraft.network.chat.Component ->
        //     net.minecraft.network.chat.MutableComponent empty()

        if (ver.getMinecraftVersion() >= 1.19) {
            // 1.19.0 = g, 1.19.1 = h
            final String methodName = ver.getRevision() == 0 ? "g" : "h";

            // net.minecraft.network.chat.Component ->
            //     net.minecraft.network.chat.MutableComponent empty()
            this.method_EmptyComponent = clazz_IChatBaseComponent.getDeclaredMethod(methodName);
        }

        // 1.18 doesn't have #empty(), instead use #nullToEmpty()
        // net.minecraft.network.chat.Component -> qk:
        //    net.minecraft.network.chat.Component nullToEmpty(java.lang.String) -> a
        this.method_TextComponent = clazz_IChatBaseComponent.getDeclaredMethod("a", String.class);

    }

    private void getMethodTranslatable() throws NoSuchMethodException {
        if (ver.getMinecraftVersion() < 1.19) {
            // 1.18 instantiates an object, so this method doesn't apply
            return;
        }

        // net.minecraft.network.chat.Component ->
        // net.minecraft.network.chat.MutableComponent translatable(java.lang.String)
        // net.minecraft.network.chat.MutableComponent translatable(java.lang.String,java.lang.Object[])

        this.method_Translatable = clazz_IChatBaseComponent.getDeclaredMethod("a");
        this.method_TranslatableWithArgs = clazz_IChatBaseComponent.getDeclaredMethod("a", String.class, Object[].class);
    }

     @SuppressWarnings("deprecation")
     public @NotNull String getTranslationKey(final @NotNull LivingEntity livingEntity){
        // only needed for spigot. paper has a built-in method

        // net.minecraft.world.entity.EntityType ->
        //    300:300:java.util.Optional byString(java.lang.String) -> a
        // public static Optional<EntityTypes<?>> byString(String s)

        Optional<?> optionalResult;
        try {
            optionalResult = (Optional<?>)this.method_EntityTypeByString.invoke(null, livingEntity.getType().getName());

            if (optionalResult.isEmpty()) {
                return "";
            }

            // net.minecraft.world.entity.EntityTypes<T extends Entity>
            return (String) method_GetDescriptionId.invoke(optionalResult.get());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return "";
    }

    private void buildSimpleMethods() throws NoSuchMethodException {
        this.method_getHandle = clazz_CraftLivingEntity.getDeclaredMethod("getHandle");

        // net.minecraft.network.syncher.SynchedEntityData getEntityData() ->
        this.method_getEntityData = clazz_Entity.getDeclaredMethod("ai");

        // set(net.minecraft.network.syncher.EntityDataAccessor,java.lang.Object) ->
        this.method_set = clazz_DataWatcher.getMethod("b", clazz_DataWatcherObject, Object.class);

        // net.minecraft.world.entity.EntityType getType() ->
        this.method_getId = clazz_Entity.getDeclaredMethod("ae");

        this.method_PlayergetHandle = clazz_CraftPlayer.getDeclaredMethod("getHandle");

        // net.minecraft.server.network.ServerGamePacketListenerImpl ->
        //    void send(net.minecraft.network.protocol.Packet) ->
        //this.method_Send = clazz_PlayerConnection.getDeclaredMethod("a", clazz_Packet);
        this.method_Send = clazz_ServerPlayerConnection.getDeclaredMethod("a", clazz_Packet);

        // java.util.List getAll() ->
        this.method_getAll = clazz_DataWatcher.getDeclaredMethod("c");

        // net.minecraft.network.syncher.SynchedEntityData ->
        //    define(net.minecraft.network.syncher.EntityDataAccessor,java.lang.Object) ->
        this.method_define = clazz_DataWatcher.getDeclaredMethod("a", clazz_DataWatcherObject, Object.class);

        // net.minecraft.network.syncher.EntityDataAccessor getAccessor() ->
        this.method_getAccessor = clazz_DataWatcher_Item.getDeclaredMethod("a");
        // java.lang.Object getValue() ->
        this.method_getValue = clazz_DataWatcher_Item.getDeclaredMethod("b");

        // net.minecraft.network.Connection getConnection() ->
        //this.method_getConnection = clazz_CraftPlayer.getDeclaredMethod("networkManager");

        if (this.hasKiori)
            this.method_AsVanilla = clazz_PaperAdventure.getDeclaredMethod("asVanilla", Component.class);

        // java.util.Optional byString(java.lang.String) -> a
        this.method_EntityTypeByString = clazz_EntityTypes.getDeclaredMethod("a", String.class);

        // java.lang.String getDescriptionId() -> g
        this.method_GetDescriptionId = clazz_EntityTypes.getDeclaredMethod("g");
    }

    private void buildFields() throws NoSuchFieldException {
        // net.minecraft.network.syncher.EntityDataSerializer OPTIONAL_COMPONENT
        this.field_OPTIONAL_COMPONENT = clazz_DataWatcherRegistry.getDeclaredField("f");

        // net.minecraft.network.syncher.EntityDataSerializer BOOLEAN
        this.field_BOOLEAN = clazz_DataWatcherRegistry.getDeclaredField("i");

        // net.minecraft.server.level.ServerPlayer ->
        //    net.minecraft.server.network.ServerGamePacketListenerImpl connection ->
        this.field_Connection = clazz_EntityPlayer.getDeclaredField("b");
    }

    private void buildConstructors() throws NoSuchMethodException {
        this.ctor_EntityDataAccessor = clazz_DataWatcherObject.getConstructor(
                int.class, clazz_DataWatcherSerializer);

        this.ctor_SynchedEntityData = clazz_DataWatcher.getConstructor(clazz_Entity);

        if (ver.getMinecraftVersion() == 1.19 && ver.getMinorVersion() <= 2 ||
            ver.getMinecraftVersion() < 1.19) {
            // up to 1.19.2 use this one:
            this.ctor_Packet = clazz_ClientboundSetEntityDataPacket.getConstructor(
                    int.class, clazz_DataWatcher, boolean.class);
        }
        else{
            this.isOneNinteenThreeOrNewer = true;
            // starting with 1.19.3 use this one:

            // public net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata(net.minecraft.network.PacketDataSerializer)
            // public net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata(int,java.util.List<DataWatcher.b<?>>)

            Class<?> clazzList;
            try {
                clazzList = Class.forName("java.util.List");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
            this.ctor_Packet = clazz_ClientboundSetEntityDataPacket.getConstructor(
                    int.class, clazzList);
        }
    }

    public boolean getHasKiori(){
        return this.hasKiori;
    }

    public @NotNull ServerVersionInfo getServerVersionInfo(){
        return this.ver;
    }

    public boolean getIsOneNinteenThreeOrNewer(){
        return this.isOneNinteenThreeOrNewer;
    }
}
