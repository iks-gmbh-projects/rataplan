package de.iks.rataplan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.iks.rataplan.domain.PasswordChange;
import de.iks.rataplan.domain.User;
import de.iks.rataplan.exceptions.InvalidTokenException;
import de.iks.rataplan.exceptions.MailAlreadyInUseException;
import de.iks.rataplan.exceptions.UsernameAlreadyInUseException;
import de.iks.rataplan.exceptions.WrongCredentialsException;
import de.iks.rataplan.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Override
	public User registerUser(User user) {
		user.trimUserCredentials();
		 if (userRepository.findOneByUsername(user.getUsername()) != null ) {
			throw new UsernameAlreadyInUseException("The username \"" + user.getUsername() + "\" is already in use!");
		} else if (userRepository.findOneByMail(user.getMail()) != null) {
			throw new MailAlreadyInUseException("The email \"" + user.getMail() + "\" is already in use!");
		} else {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setId(null);
			return userRepository.saveAndFlush(user);
		}
	}

	@Override
	public User loginUser(User user) {
		user.trimUserCredentials();
		User dbUser;
		if (user.getUsername() != null) {
			dbUser = userRepository.findOneByUsername(user.getUsername());
		} else {
			dbUser = userRepository.findOneByMail(user.getMail());
		}
		
		if (dbUser != null && passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
			return dbUser;
		} else {
			throw new WrongCredentialsException("These credentials have no match!");
		}
	}
	
	@Override
	public User getUserData(String username) {
		User dbUser;
		if (username != null) {
			dbUser = userRepository.findOneByUsername(username);
			return dbUser;
		}
		throw new InvalidTokenException("Token is not allowed to get data.");
	}

	@Override
	public Boolean changePassword(String username, PasswordChange passwords) {
		User user = this.getUserData(username);
		if (user != null && passwordEncoder.matches(passwords.getOldPassword(), user.getPassword())) {
			user.setPassword(passwordEncoder.encode(passwords.getNewPassword()));
			userRepository.saveAndFlush(user);
			return true;
		} else {
			return false;
		}
	}
	
}
