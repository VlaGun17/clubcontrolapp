package com.vladi.clubcontrolapp.infrastructure.session;

import com.vladi.clubcontrolapp.domain.entities.Admin;
import com.vladi.clubcontrolapp.domain.entities.Client;
import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.entities.Payment;
import com.vladi.clubcontrolapp.domain.entities.Service;
import com.vladi.clubcontrolapp.domain.entities.Session;
import com.vladi.clubcontrolapp.domain.entities.SessionService;
import com.vladi.clubcontrolapp.domain.entities.Tariff;
import com.vladi.clubcontrolapp.domain.enums.ComputerStatus;
import com.vladi.clubcontrolapp.domain.enums.ComputerType;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.AdminRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.ClientRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.ComputerRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.PaymentRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.ServiceRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.SessionRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.SessionServiceRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.TariffRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.impl.AdminRepositoryImpl;
import com.vladi.clubcontrolapp.infrastructure.persistance.impl.ClientRepositoryImpl;
import com.vladi.clubcontrolapp.infrastructure.persistance.impl.ComputerRepositoryImpl;
import com.vladi.clubcontrolapp.infrastructure.persistance.impl.PaymentRepositoryImpl;
import com.vladi.clubcontrolapp.infrastructure.persistance.impl.ServiceRepositoryImpl;
import com.vladi.clubcontrolapp.infrastructure.persistance.impl.SessionRepositoryImpl;
import com.vladi.clubcontrolapp.infrastructure.persistance.impl.SessionServiceRepositoryImpl;
import com.vladi.clubcontrolapp.infrastructure.persistance.impl.TariffRepositoryImpl;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.ConnectionManager;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.CachedJdbcRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.decorators.CachedAdminRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.decorators.CachedClientRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.decorators.CachedComputerRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.decorators.CachedPaymentRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.decorators.CachedServiceRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.decorators.CachedSessionRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.decorators.CachedSessionServiceRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.decorators.CachedTariffRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.uow.JdbcUnitOfWork;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PersistanceSession {
  private final ConnectionManager connectionManager;
  private final JdbcUnitOfWork unitOfWork;

  private final ClientRepository clientRepository;
  private final AdminRepository adminRepository;
  private final ComputerRepository computerRepository;
  private final TariffRepository tariffRepository;
  private final ServiceRepository serviceRepository;
  private final SessionRepository sessionRepository;
  private final PaymentRepository paymentRepository;
  private final SessionServiceRepository sessionServiceRepository;

  public PersistanceSession(ConnectionManager connectionManager){
    this.connectionManager = connectionManager;
    ClientRepository baseClientRepo = new ClientRepositoryImpl(connectionManager);
    AdminRepository baseAdminRepo = new AdminRepositoryImpl(connectionManager);
    ComputerRepository baseComputerRepo = new ComputerRepositoryImpl(connectionManager);
    TariffRepository baseTariffRepo = new TariffRepositoryImpl(connectionManager);
    ServiceRepository baseServiceRepo = new ServiceRepositoryImpl(connectionManager);
    SessionRepository baseSessionRepo = new SessionRepositoryImpl(connectionManager);
    PaymentRepository basePaymentRepo = new PaymentRepositoryImpl(connectionManager);
    SessionServiceRepository baseSessionServiceRepo = new SessionServiceRepositoryImpl(connectionManager);

    this.clientRepository = new CachedClientRepository(baseClientRepo);
    this.adminRepository = new CachedAdminRepository(baseAdminRepo);
    this.computerRepository = new CachedComputerRepository(baseComputerRepo);
    this.tariffRepository = new CachedTariffRepository(baseTariffRepo);
    this.serviceRepository = new CachedServiceRepository(baseServiceRepo);
    this.sessionRepository = new CachedSessionRepository(baseSessionRepo);
    this.paymentRepository = new CachedPaymentRepository(basePaymentRepo);
    this.sessionServiceRepository = new CachedSessionServiceRepository(baseSessionServiceRepo);

    this.unitOfWork = new JdbcUnitOfWork(connectionManager, clientRepository, adminRepository, computerRepository, paymentRepository, serviceRepository, sessionRepository, tariffRepository, sessionServiceRepository);
  }

  // --- CLIENTS ---
  public void addClient(Client client) {
    unitOfWork.registerNew(client);
  }

  public Optional<Client> getClient(UUID id) {
    return clientRepository.findById(id);
  }

  public Optional<Client> getClientByEmail(String email) {
    return clientRepository.findByEmail(email);
  }

  public List<Client> findClientsByName(String name) {
    return clientRepository.findByNameContaining(name);
  }

  public List<Client> findClientsByRegistrationDate(LocalDate registrationDate){
    return clientRepository.findByRegistrationDate(registrationDate);
  }

  public void updateClient(Client client) {
    unitOfWork.registerDirty(client);
  }

  public void removeClient(Client client) {
    unitOfWork.registerDeleted(client);
  }

  public List<Client> getAllClients() { return clientRepository.findAll(); }

  public Optional<Client> getClientByLogin(String login) { return clientRepository.findByLogin(login); }

  // --- COMPUTERS ---
  public Optional<Computer> getComputer(UUID id) {
    return computerRepository.findById(id);
  }

  public void addComputer(Computer computer) {
    unitOfWork.registerNew(computer);
  }

  public void updateComputer(Computer computer) {
    unitOfWork.registerDirty(computer);
  }

  public void removeComputer(Computer computer) {
    unitOfWork.registerDeleted(computer);
  }

  public Optional<Computer> getComputerByNumber(int number) {
    return computerRepository.findByNumber(number);
  }

  public List<Computer> getComputersByType(ComputerType type) {
    return computerRepository.findByComputerType(type);
  }

  public List<Computer> getComputerByStatus(ComputerStatus computerStatus){
    return computerRepository.findByComputerStatus(computerStatus);
  }

  public List<Computer> getAllComputers(){
    return computerRepository.findAll();
  }

  // --- ADMINS ---
  public Optional<Admin> getAdmin(UUID id) {
    return adminRepository.findById(id);
  }

  public void addAdmin(Admin admin) {
    unitOfWork.registerNew(admin);
  }

  public void updateAdmin(Admin admin) {
    unitOfWork.registerDirty(admin);
  }

  public void removeAdmin(Admin admin) {
    unitOfWork.registerDeleted(admin);
  }

  public Optional<Admin> getAdminByLogin(String login) {
    return adminRepository.findByLogin(login);
  }
  public Optional<Admin> findById(UUID id){
    return adminRepository.findById(id);
  }

  // --- TARIFFS ---
  public Optional<Tariff> getTariff(UUID id) {
    return tariffRepository.findById(id);
  }

  public void addTariff(Tariff tariff) {
    unitOfWork.registerNew(tariff);
  }

  public void updateTariff(Tariff tariff) {
    unitOfWork.registerDirty(tariff);
  }

  public void removeTariff(Tariff tariff) {
    unitOfWork.registerDeleted(tariff);
  }

  public Optional<Tariff> getCurrentTariff() {
    return tariffRepository.findCurrentTariff(LocalDate.now());
  }

  public List<Tariff> getNightTariffs() {
    return tariffRepository.findNightTariffs();
  }

  public Optional<Tariff> getTariffByName(String name) {
    return tariffRepository.findByName(name);
  }

  public List<Tariff> getAllTariffs() { return tariffRepository.findAll(); }

  // --- SERVICES ---
  public Optional<Service> getService(UUID id) {
    return serviceRepository.findById(id);
  }

  public void addService(Service service){
    unitOfWork.registerNew(service);
  }

  public void updateService(Service service){
    unitOfWork.registerDirty(service);
  }

  public void removeService(Service service){
    unitOfWork.registerDeleted(service);
  }

  public Optional<Service> getServiceByName(String name) {
    return serviceRepository.findByName(name);
  }

  public List<Service> getServicesInPriceRange(BigDecimal min, BigDecimal max) {
    return serviceRepository.findByPriceRange(min, max);
  }

  public List<Service> getAllServices(){ return serviceRepository.findAll(); }

  // --- SESSIONS ---
  public Optional<Session> getSession(UUID id) {
    return sessionRepository.findById(id);
  }

  public void addSession(Session session){
    unitOfWork.registerNew(session);
  }

  public void updateSession(Session session){
    unitOfWork.registerDirty(session);
  }

  public void removeSession(Session session){
    unitOfWork.registerDeleted(session);
  }

  public List<Session> getActiveSessions() {
    return sessionRepository.findAllActive();
  }

  public List<Computer> getOccupiedComputers() {
    return getActiveSessions().stream()
        .map(session -> getComputer(session.getComputerId()))
        .flatMap(Optional::stream)
        .toList();
  }

  public void addServiceToSession(Session session, SessionService sessionService){
    unitOfWork.registerNew(sessionService);
    session.getServices().add(sessionService);
  }

  public List<SessionService> getServicesForSession(UUID sessionId) {
    return sessionServiceRepository.findBySessionId(sessionId);
  }

  public List<Session> getAllSession(){ return sessionRepository.findAll(); }

  // --- PAYMENTS ---
  public Optional<Payment> getPaymentBySession(UUID sessionId) {
    return paymentRepository.findBySessionId(sessionId);
  }

  public Optional<Payment> getPayment(UUID id){
    return paymentRepository.findById(id);
  }

  public void addPayments(Payment payment){
    unitOfWork.registerNew(payment);
  }

  public void updatePayment(Payment payment){
    unitOfWork.registerDirty(payment);
  }

  public void removePayment(Payment payment){
    unitOfWork.registerDeleted(payment);
  }

  public BigDecimal getDailyRevenue() {
    return paymentRepository.getTotalRevenue(LocalDate.now());
  }

  public List<Payment> getAllPayments() { return paymentRepository.findAll(); }

  public void commit(){
    unitOfWork.commit();
  }

  public void rollback() { unitOfWork.rollback(); }
}
