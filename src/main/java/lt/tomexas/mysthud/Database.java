package lt.tomexas.mysthud;

import lt.tomexas.mysthud.playerfontimage.impl.MinotarSource;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;

import java.sql.*;

public class Database {

    private static Connection connection;

    public Database(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);

        try(Statement statement = connection.createStatement();) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS players
                (uuid TEXT PRIMARY KEY,
                playerHead TEXT NOT NULL,
                playerBust TEXT NOT NULL)
            """);
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void addPlayer(Player player) {
        if (playerExists(player)) updatePlayer(player);
        else {
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (uuid, playerHead, playerBust) VALUES (?,?,?)")) {
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2,
                        Main.get().getPlayerFontImage().getHead(player.getUniqueId(), true, new MinotarSource(true)).toString()
                );
                preparedStatement.setString(3,
                        Main.get().getPlayerFontImage().getBust(player.getUniqueId(), true, new MinotarSource(true)).toString()
                );
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addNPC(NPC npc) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (uuid, playerHead, playerBust) VALUES (?,?,?)")){
            preparedStatement.setString(1, npc.getEntity().getUniqueId().toString());
            preparedStatement.setString(2,
                    Main.get().getPlayerFontImage().getHead(npc, new MinotarSource()).toString()
            );
            preparedStatement.setString(3,
                    Main.get().getPlayerFontImage().getHead(npc, new MinotarSource()).toString()
            );
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeNPC(NPC npc) {
        if (!npcExists(npc)) return;
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM players WHERE uuid = ?")){
            preparedStatement.setString(1, npc.getEntity().getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(Player player) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")){
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean npcExists(NPC npc) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")){
            preparedStatement.setString(1, npc.getEntity().getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updatePlayer(Player player) {
        if (playerExists(player)) return;
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET playerHead = ?, playerBust = ? WHERE uuid = ?")){
            preparedStatement.setString(1,
                    Main.get().getPlayerFontImage().getHead(player.getUniqueId(), true, new MinotarSource(true)).toString()
            );
            preparedStatement.setString(2,
                    Main.get().getPlayerFontImage().getBust(player.getUniqueId(), true, new MinotarSource(true)).toString()
            );
            preparedStatement.setString(3, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getPlayerHead(Player player) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT playerHead FROM players WHERE uuid = ?")){
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return resultSet.getString("playerHead");
            else return "";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getPlayerBust(Player player) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT playerBust FROM players WHERE uuid = ?")){
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return resultSet.getString("playerBust");
            else return "";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getPlayerBust(NPC npc) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT playerBust FROM players WHERE uuid = ?")){
            preparedStatement.setString(1, npc.getEntity().getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return resultSet.getString("playerBust");
            else return "";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
}
