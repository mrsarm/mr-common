package mr.common.security.userentity.organization.service;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import mr.common.model.ConfigurableData;
import mr.common.security.exception.UserLockedException;
import mr.common.security.model.User;
import mr.common.security.organization.exception.DuplicatedOrganizationException;
import mr.common.security.organization.exception.InvalidOrganizationNameException;
import mr.common.security.organization.exception.OrganizationLockedException;
import mr.common.security.organization.exception.OrganizationNotExistException;
import mr.common.security.organization.exception.UserIsInOrganizationException;
import mr.common.security.organization.exception.UserNotInOrganizationException;
import mr.common.security.organization.model.Organization;
import mr.common.security.organization.service.OrganizationService;
import mr.common.security.service.UserService;
import mr.common.security.userentity.model.UserEntity;
import mr.common.security.userentity.organization.dao.OrganizationEntityDao;
import mr.common.security.userentity.organization.dao.UserOrganizationDao;
import mr.common.security.userentity.organization.model.OrganizationEntity;
import mr.common.security.userentity.organization.model.UserOrganization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


/**
 * Implementación de {@link mr.common.security.organization.
 * service.OrganizationService OrganizationService}.
 *
 * @see mr.common.security.userentity.organization.model.OrganizationEntity
 * @author Mariano Ruiz
 */
public class OrganizationEntityService implements OrganizationService {

	@Resource
	private OrganizationEntityDao orgDao;

	@Resource
	private UserOrganizationDao userOrganizationDao;

	@Autowired
	private UserService userService;


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional(readOnly = true)
	public List getList() {
		return orgDao.getList();
	}

	@Transactional(readOnly = true)
	public int count() {
		return (int) orgDao.count();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional(readOnly = true)
	public List find(String nameOrDescription, Boolean activeFilter,
			ConfigurableData page) {
		return orgDao.find(nameOrDescription, null, activeFilter, page);
	}

	@Transactional(readOnly = true)
	public int findCount(String nameOrDescription, Boolean activeFilter) {
		return orgDao.findCount(nameOrDescription, null, activeFilter);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional(readOnly = true)
	public List find(String nameOrDescription, Serializable userId, Boolean activeFilter,
			ConfigurableData page) {
		userService.getUsernameById(userId); // Lanza una excepción si no existe el usuario
		return orgDao.find(nameOrDescription, userId, activeFilter, page);
	}

	@Transactional(readOnly = true)
	public int findCount(String nameOrDescription, Serializable userId, Boolean activeFilter) {
		return orgDao.findCount(nameOrDescription, userId, activeFilter);
	}

	@Transactional(readOnly = true)
	public Organization getById(Serializable id) {
    	return getById(id, true);
	}

	@Transactional(readOnly = true)
	protected Organization getById(Serializable id, boolean readOnly) {
    	if(id==null) {
    		throw new NullPointerException("id = null.");
    	}
    	Organization org = orgDao.get((Long)id);
		if(org==null) {
			throw new OrganizationNotExistException(
					"Organization with id=" + id.toString() + " not exist.");
		}
		if(!readOnly && org.isLocked()) {
			throw new OrganizationLockedException();
		}
		return org;
	}

	@Transactional(readOnly = true)
	public Organization getByName(String name) {
    	if(name==null) {
    		throw new NullPointerException("name = null.");
    	}
    	Organization org = orgDao.getByName(name);
		if(org==null) {
			throw new OrganizationNotExistException(
					"Organization with name=" + name + " not exist.");
		}
		return org;
	}

	@Transactional(readOnly = false)
	public Organization newOrganization(Organization org) {
		if(org.getId()!=null) {
			throw new IllegalArgumentException(
					"New organization should not have set the id.");
		}
		return saveOrUpdate((OrganizationEntity)org);
	}

	@Transactional(readOnly = false)
	private Organization saveOrUpdate(OrganizationEntity org) {
		OrganizationEntity organization;
		if(org.getId()!=null) {
			organization = (OrganizationEntity) getById(org.getId(), false);
		} else {
			organization = new OrganizationEntity();
		}
		if(isValidOrganizationName(org.getName())) {
			organization.setName(org.getName());
		} else {
			throw new InvalidOrganizationNameException(org.getName());
		}
		OrganizationEntity duplicate = (OrganizationEntity) orgDao.getByName(org.getName());
		if(duplicate!=null && (org.getId()==null
				|| (org.getId()!=null && !org.getId().equals(duplicate.getId())))) {
			throw new DuplicatedOrganizationException();
		}
		organization.setEnabled(org.isEnabled());
		organization.setDescription(org.getDescription());
		organization.setTimeZoneId(org.getTimeZoneId());
		organization.setCountryId(org.getCountryId());
		organization.setCityOrRegionName(org.getCityOrRegionName());
		organization.setStateOrProvinceName(org.getStateOrProvinceName());
		organization.setPostalAddress(org.getPostalAddress());
		organization.setPostalCode(org.getPostalCode());
		organization.setTelephoneNumber(org.getTelephoneNumber());
		organization.setLogoId(org.getLogoId());
		return orgDao.merge(organization);
	}

	@Transactional(readOnly = false)
	public Organization updateOrganization(Serializable id, Organization organization) {
		if(id==null) {
			throw new NullPointerException(
					"id = null.");
		}
		if(organization.getId()!=null) {
			throw new IllegalArgumentException(
					"Argument `organization` should not have set the id.");
		}
		OrganizationEntity organizationEntity = (OrganizationEntity) organization;
		organizationEntity.setId((Long)id);
		return saveOrUpdate((OrganizationEntity)organization);
	}

	@Transactional(readOnly = false)
	public Organization updateOrganization(String name, Organization organization) {
		if(name==null) {
			throw new NullPointerException(
					"name = null.");
		}
		if(organization.getId()!=null) {
			throw new IllegalArgumentException(
					"Argument `organization` should not have set the id.");
		}
		OrganizationEntity organizationEntity = (OrganizationEntity) organization;
		organizationEntity.setId(getByName(name).getId());
		return saveOrUpdate((OrganizationEntity)organization);
	}

	public Organization getOrganizationInstance() {
		return new OrganizationEntity();
	}

	public boolean isValidOrganizationName(String name) {
		if(!name.equals(name.trim())) {
			return false;
		}
		return true;
	}

	@Transactional(readOnly = false)
	public void deleteByName(String name) {
		Long orgId = (Long) getIdByName(name);
		userOrganizationDao.removeAllUsersFromOrganization(orgId);
		orgDao.delete((OrganizationEntity) getById(orgId, false));
	}

	@Transactional(readOnly = false)
	public void deleteById(Serializable id) {
		userOrganizationDao.removeAllUsersFromOrganization((Long) id);
		orgDao.delete((OrganizationEntity) getById(id, false));
	}

	@Transactional(readOnly = false)
	public void updateLogoId(Serializable orgId, Serializable newLogoId) {
		OrganizationEntity org = (OrganizationEntity) getById(orgId, false);
		org.setLogoId((Long)newLogoId);
		orgDao.update(org);
	}

	@Transactional(readOnly = false)
	public void updateLock(Serializable orgId, boolean lock) {
		OrganizationEntity org = (OrganizationEntity) getById(orgId);
		org.setLocked(lock);
		orgDao.update(org);
	}

	@Transactional(readOnly = false)
	public void addUser(Serializable orgId, Serializable userId) {
		UserEntity user = (UserEntity) userService.getById(userId);
		OrganizationEntity org = (OrganizationEntity) getById(orgId);
		if(user.isLocked()) {
			throw new UserLockedException();
		}
		if(isUserInOrganization(orgId, userId)) {
			throw new  UserIsInOrganizationException(
				"User with id " + orgId.toString() + " is in organization with id "
				+ orgId.toString() + ".");
		}
		UserOrganization userOrganization = new UserOrganization();
		userOrganization.setUser(user);
		userOrganization.setOrganization(org);
		userOrganizationDao.save(userOrganization);
	}

	@Transactional(readOnly = false)
	public void removeUser(Serializable orgId, Serializable userId) {
		UserEntity user = (UserEntity) userService.getById(userId);
		if(user.isLocked()) {
			throw new UserLockedException();
		}
		getNameById(orgId); // Si la organización no existe lanza excepción
		Long id = userOrganizationDao.getUserOrganizationId((Long)orgId, (Long)userId);
		if(id==null) {
			throw new UserNotInOrganizationException(
				"User with id=" + userId + " is not in organization with id=" + orgId + ".");
		}
		userOrganizationDao.deleteById(id);
	}

	@Transactional(readOnly = false)
	public int removeUserFromAll(Serializable userId) {
		UserEntity user = (UserEntity) userService.getById(userId);
		if(user.isLocked()) {
			throw new UserLockedException();
		}
		return userOrganizationDao.removeUserFromAll((Long)userId);
	}

	@Transactional(readOnly = false)
	public int removeAllUsersFromOrganization(Long id) {
		getNameById(id); // Si la organización no existe lanza excepción
		return userOrganizationDao.removeAllUsersFromOrganization(id);
	}

	@Transactional(readOnly = true)
	public boolean isUserInOrganization(Serializable orgId, Serializable userId) {
		userService.getUsernameById(userId); // Si el user no existe lanza excepción
		getNameById(orgId); // Si la organización no existe lanza excepción
		return userOrganizationDao.getUserOrganizationId((Long)orgId, (Long)userId) != null;
	}

	@Transactional(readOnly = true)
	public List<Organization> getUserOrganizations(Serializable userId) {
		userService.getUsernameById(userId); // Si el user no existe lanza excepción
		return userOrganizationDao.getUserOrganizations((Long)userId);
	}

	@Transactional(readOnly = true)
	public int getUserOrganizationsCount(Serializable userId) {
		userService.getUsernameById(userId); // Si el user no existe lanza excepción
		return userOrganizationDao.getUserOrganizationsCount((Long)userId);
	}

	@Transactional(readOnly = true)
	public String getNameById(Serializable orgId) {
		if(orgId==null) {
			throw new NullPointerException(
					"orgId = null.");
		}
		String name = orgDao.getNameById(orgId);
		if(name==null) {
			throw new OrganizationNotExistException(
					"Organization with id=" + orgId.toString() + " not exist.");
		}
		return name;
	}

	@Transactional(readOnly = true)
	public Serializable getIdByName(String name) {
		if(name==null) {
			throw new NullPointerException(
					"name = null.");
		}
		Long id = orgDao.getIdByName(name);
		if(id==null) {
			throw new OrganizationNotExistException(
					"Organization with name=" + name + " not exist.");
		}
		return id;
	}

	@SuppressWarnings("unchecked")
	public List<Serializable> getUserOrganizationsId(Serializable userId) {
		userService.getUsernameById(userId); // Si el user no existe lanza excepción
		return userOrganizationDao.getUserOrganizationsId((Long)userId);
	}

	@Transactional(readOnly = true)
	public List<User> getUsers(Serializable id) {
		getNameById(id); // Si la organización no existe lanza excepción
		return userOrganizationDao.getUsers(id);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Serializable> getUsersId(Serializable id) {
		getNameById(id); // Si la organización no existe lanza excepción
		return userOrganizationDao.getUsersId(id);
	}

	@Transactional(readOnly = true)
	public int getUsersCount(Serializable id) {
		getNameById(id); // Si la organización no existe lanza excepción
		return userOrganizationDao.getUsersCount(id);
	}
}
