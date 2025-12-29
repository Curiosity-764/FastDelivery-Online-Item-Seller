package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

public class PasswordUtil {

	// SHA-256 based password hashing
	private static final String ALGORITHM = "SHA-256";
	private static final int SALT_LENGTH = 16; // 128 bits
	private static final int ITERATIONS = 10000;

	public static String hashPassword(String password) {
		try {
			byte[] salt = generateSalt();

			byte[] hash = hashWithSalt(password, salt, ITERATIONS);

			byte[] saltPlusHash = new byte[salt.length + hash.length];
			System.arraycopy(salt, 0, saltPlusHash, 0, salt.length);
			System.arraycopy(hash, 0, saltPlusHash, salt.length, hash.length);

			return Base64.getEncoder().encodeToString(saltPlusHash);

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Error hashing password", e);
		}
	}


	public static boolean verifyPassword(String password, String storedHash) {
		try {
			try {
				byte[] saltPlusHash = Base64.getDecoder().decode(storedHash);
				if (saltPlusHash.length > SALT_LENGTH) {
					byte[] salt = new byte[SALT_LENGTH];
					System.arraycopy(saltPlusHash, 0, salt, 0, SALT_LENGTH);

					byte[] storedHashBytes = new byte[saltPlusHash.length - SALT_LENGTH];
					System.arraycopy(saltPlusHash, SALT_LENGTH, storedHashBytes, 0, storedHashBytes.length);

					byte[] testHash = hashWithSalt(password, salt, ITERATIONS);

					// Compare hashes
					return MessageDigest.isEqual(storedHashBytes, testHash);
				}
			} catch (IllegalArgumentException e) {
			}

			if (storedHash.length() == 64 && storedHash.matches("[0-9a-fA-F]{64}")) {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hashBytes = digest.digest(password.getBytes());

				StringBuilder hexString = new StringBuilder();
				for (byte b : hashBytes) {
					String hex = Integer.toHexString(0xff & b);
					if (hex.length() == 1)
						hexString.append('0');
					hexString.append(hex);
				}

				return hexString.toString().equalsIgnoreCase(storedHash);
			}

			return false;

		} catch (Exception e) {
			System.out.println("❌ Error verifying password: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Hash password with salt and iterations
	 */
	private static byte[] hashWithSalt(String password, byte[] salt, int iterations) throws NoSuchAlgorithmException {

		MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
		digest.reset();
		digest.update(salt);

		byte[] hash = digest.digest(password.getBytes());

		// Apply multiple iterations
		for (int i = 1; i < iterations; i++) {
			digest.reset();
			hash = digest.digest(hash);
		}

		return hash;
	}

	/**
	 * Generate random salt
	 */
	private static byte[] generateSalt() {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[SALT_LENGTH];
		random.nextBytes(salt);
		return salt;
	}

	/**
	 * Check password complexity
	 */
	public static ValidationResult validatePasswordComplexity(String password) {
		ValidationResult result = new ValidationResult();

		if (password == null || password.length() < 8) {
			result.addError("Password must be at least 8 characters long");
		}

		if (!password.matches(".*[A-Z].*")) {
			result.addError("Password must contain at least one uppercase letter");
		}

		if (!password.matches(".*[a-z].*")) {
			result.addError("Password must contain at least one lowercase letter");
		}

		if (!password.matches(".*\\d.*")) {
			result.addError("Password must contain at least one number");
		}

		if (!password.matches(".*[@#$%^&+=!].*")) {
			result.addError("Password must contain at least one special character (@#$%^&+=!)");
		}

		return result;
	}

	/**
	 * Generate random password
	 */
	public static String generateRandomPassword() {
		String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String lower = "abcdefghijklmnopqrstuvwxyz";
		String digits = "0123456789";
		String special = "@#$%^&+=!";
		String all = upper + lower + digits + special;

		SecureRandom random = new SecureRandom();
		StringBuilder password = new StringBuilder();

		// Ensure at least one of each type
		password.append(upper.charAt(random.nextInt(upper.length())));
		password.append(lower.charAt(random.nextInt(lower.length())));
		password.append(digits.charAt(random.nextInt(digits.length())));
		password.append(special.charAt(random.nextInt(special.length())));

		// Fill remaining with random characters
		for (int i = 0; i < 8; i++) {
			password.append(all.charAt(random.nextInt(all.length())));
		}

		// Shuffle the password
		char[] chars = password.toString().toCharArray();
		for (int i = chars.length - 1; i > 0; i--) {
			int j = random.nextInt(i + 1);
			char temp = chars[i];
			chars[i] = chars[j];
			chars[j] = temp;
		}

		return new String(chars);
	}

	/**
	 * Validation result class
	 */
	public static class ValidationResult {
		private boolean valid = true;
		private StringBuilder errorMessages = new StringBuilder();

		public void addError(String error) {
			valid = false;
			if (errorMessages.length() > 0) {
				errorMessages.append("<br>");
			}
			errorMessages.append(error);
		}

		public boolean isValid() {
			return valid;
		}

		public String getErrorMessages() {
			return errorMessages.toString();
		}
	}
}