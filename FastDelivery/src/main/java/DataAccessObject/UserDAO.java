package DataAccessObject;

import model.User;
import model.DBConnection;
import utils.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

	private static final String INSERT_USER = "INSERT INTO users (username, email, password_hash, first_name, last_name, phone, address, role, created_at, active) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE user_id = ? AND active = 1";

	private static final String SELECT_USER_BY_USERNAME = "SELECT * FROM users WHERE username = ? AND active = 1";

	private static final String SELECT_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ? AND active = 1";

	private static final String SELECT_ALL_USERS = "SELECT * FROM users WHERE active = 1 ORDER BY created_at DESC";

	private static final String SELECT_ALL_USERS_ADMIN = "SELECT * FROM users ORDER BY created_at DESC";

	private static final String UPDATE_USER = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone = ?, address = ? WHERE user_id = ?";

	private static final String UPDATE_USER_PASSWORD = "UPDATE users SET password_hash = ? WHERE user_id = ?";

	private static final String DELETE_USER = "UPDATE users SET active = 0 WHERE user_id = ?";

	private static final String ACTIVATE_USER = "UPDATE users SET active = 1 WHERE user_id = ?";

	private static final String CHANGE_USER_ROLE = "UPDATE users SET role = ? WHERE user_id = ?";

	private static final String COUNT_USERS = "SELECT COUNT(*) FROM users WHERE active = 1";

	public int createUser(User user) throws SQLException {
		int generatedId = -1;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;

		try {
			connection = DBConnection.getConnection();
			System.out.println("✅ UserDAO: Connection established for user registration");

			preparedStatement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);

			preparedStatement.setString(1, user.getUsername());
			preparedStatement.setString(2, user.getEmail());
			preparedStatement.setString(3, user.getPasswordHash());
			preparedStatement.setString(4, user.getFirstName());
			preparedStatement.setString(5, user.getLastName());
			preparedStatement.setString(6, user.getPhone());
			preparedStatement.setString(7, user.getAddress());
			preparedStatement.setString(8, user.getRole());
			preparedStatement.setTimestamp(9, new Timestamp(user.getCreatedAt().getTime()));
			preparedStatement.setBoolean(10, user.isActive());

			System.out.println("✅ UserDAO: Executing insert with values:");
			System.out.println("   Username: " + user.getUsername());
			System.out.println("   Email: " + user.getEmail());
			System.out.println("   FirstName: " + user.getFirstName());
			System.out.println("   Role: " + user.getRole());

			int affectedRows = preparedStatement.executeUpdate();

			if (affectedRows > 0) {
				generatedKeys = preparedStatement.getGeneratedKeys();
				if (generatedKeys != null && generatedKeys.next()) {
					generatedId = generatedKeys.getInt(1);
					user.setUserId(generatedId);
				}
			}

		} catch (SQLException e) {
			System.out.println("❌ UserDAO: SQL Error creating user:");
			System.out.println("   Error Code: " + e.getErrorCode());
			System.out.println("   SQL State: " + e.getSQLState());
			System.out.println("   Message: " + e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			if (generatedKeys != null)
				try {
					generatedKeys.close();
				} catch (SQLException e) {
				}
			if (preparedStatement != null)
				try {
					preparedStatement.close();
				} catch (SQLException e) {
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
				}
		}

		return generatedId;
	}

	public User findById(int userId) {
		User user = null;

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID)) {

			preparedStatement.setInt(1, userId);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				user = mapResultSetToUser(resultSet);
			}

		} catch (SQLException e) {
			System.out.println("Error finding user by ID: " + e.getMessage());
			e.printStackTrace();
		}

		return user;
	}

	public User findByUsername(String username) {
		User user = null;

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_USERNAME)) {

			preparedStatement.setString(1, username);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				user = mapResultSetToUser(resultSet);
			}

		} catch (SQLException e) {
			System.out.println("Error finding user by username: " + e.getMessage());
			e.printStackTrace();
		}

		return user;
	}

	public User findByEmail(String email) {
		User user = null;

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_EMAIL)) {

			preparedStatement.setString(1, email);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				user = mapResultSetToUser(resultSet);
			}

		} catch (SQLException e) {
			System.out.println("Error finding user by email: " + e.getMessage());
			e.printStackTrace();
		}

		return user;
	}

	public List<User> getAllUsers(boolean includeInactive) {
		List<User> users = new ArrayList<>();
		String sql = includeInactive ? SELECT_ALL_USERS_ADMIN : SELECT_ALL_USERS;

		try (Connection connection = DBConnection.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {

			while (resultSet.next()) {
				users.add(mapResultSetToUser(resultSet));
			}

		} catch (SQLException e) {
			System.out.println("Error getting all users: " + e.getMessage());
			e.printStackTrace();
		}

		return users;
	}

// update user
	public boolean updateUser(User user) {
		boolean rowUpdated = false;

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER)) {

			preparedStatement.setString(1, user.getFirstName());
			preparedStatement.setString(2, user.getLastName());
			preparedStatement.setString(3, user.getEmail());
			preparedStatement.setString(4, user.getPhone());
			preparedStatement.setString(5, user.getAddress());
			preparedStatement.setInt(6, user.getUserId());

			rowUpdated = preparedStatement.executeUpdate() > 0;

		} catch (SQLException e) {
			System.out.println("Error updating user: " + e.getMessage());
			e.printStackTrace();
		}

		return rowUpdated;
	}

	// Update pass
	public boolean updatePassword(int userId, String newPasswordHash) {
		boolean rowUpdated = false;

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_PASSWORD)) {

			preparedStatement.setString(1, newPasswordHash);
			preparedStatement.setInt(2, userId);

			rowUpdated = preparedStatement.executeUpdate() > 0;

		} catch (SQLException e) {
			System.out.println("Error updating password: " + e.getMessage());
			e.printStackTrace();
		}

		return rowUpdated;
	}

	public boolean deleteUser(int userId) {
		boolean rowDeleted = false;

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER)) {

			preparedStatement.setInt(1, userId);
			rowDeleted = preparedStatement.executeUpdate() > 0;

		} catch (SQLException e) {
			System.out.println("Error deleting user: " + e.getMessage());
			e.printStackTrace();
		}

		return rowDeleted;
	}

// check xem user nao dang hoat dong
	public boolean activateUser(int userId) {
		boolean rowUpdated = false;

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(ACTIVATE_USER)) {

			preparedStatement.setInt(1, userId);
			rowUpdated = preparedStatement.executeUpdate() > 0;

		} catch (SQLException e) {
			System.out.println("Error activating user: " + e.getMessage());
			e.printStackTrace();
		}

		return rowUpdated;
	}

	// thay doi vai tro user (admin only)
	public boolean changeUserRole(int userId, String newRole) {
		boolean rowUpdated = false;

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(CHANGE_USER_ROLE)) {

			preparedStatement.setString(1, newRole);
			preparedStatement.setInt(2, userId);
			rowUpdated = preparedStatement.executeUpdate() > 0;

		} catch (SQLException e) {
			System.out.println("Error changing user role: " + e.getMessage());
			e.printStackTrace();
		}

		return rowUpdated;
	}

	public boolean usernameExists(String username) {
		return findByUsername(username) != null;
	}

	public boolean emailExists(String email) {
		return findByEmail(email) != null;
	}

	public int getTotalUsers() {
		int count = 0;

		try (Connection connection = DBConnection.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(COUNT_USERS)) {

			if (resultSet.next()) {
				count = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			System.out.println("Error counting users: " + e.getMessage());
			e.printStackTrace();
		}

		return count;
	}

	private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
		User user = new User();

		user.setUserId(resultSet.getInt("user_id"));
		user.setUsername(resultSet.getString("username"));
		user.setEmail(resultSet.getString("email"));
		user.setPasswordHash(resultSet.getString("password_hash"));
		user.setFirstName(resultSet.getString("first_name"));
		user.setLastName(resultSet.getString("last_name"));
		user.setPhone(resultSet.getString("phone"));
		user.setAddress(resultSet.getString("address"));
		user.setRole(resultSet.getString("role"));
		user.setCreatedAt(resultSet.getTimestamp("created_at"));
		user.setActive(resultSet.getBoolean("active"));

		return user;
	}
}