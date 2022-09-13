package calendar.entries;

import exceptions.*;

public class Record {

    private Customer customer;
    private String customerName;
    private Address address;
    private City city;
    private Country country;


    public Record(Customer customer) {
        this.customer = customer;
        this.customerName = customer.getCustomerName();
    }

    public Record(Customer customer, Address address, City city, Country country) {
        this.customer = customer;
        this.address = address;
        this.city = city;
        this.country = country;
    }

    public static Record createNewRecord(String customerName, String addressLine, String addressLine2, String city,
                                         String country, String postalCode, String phoneNumber) throws ContactAlreadyExistsException {
        boolean alreadyExists = false;

        if(Country.alreadyExists(country)) {
            int countryId = Country.getCountryId(country);

            if(City.alreadyExists(city, countryId)) {
                int cityId = City.getCityId(city, countryId);

                if(Address.alreadyExists(addressLine, addressLine2, cityId, postalCode, phoneNumber)) {
                    int addressId = Address.getAddressId(addressLine, addressLine2, cityId, postalCode, phoneNumber);

                    if(Customer.alreadyExists(customerName, addressId)) {
                        alreadyExists = true;
                    }
                }
            }
        }

        Record record;
        Customer customerEntry;
        Address addressEntry;
        City cityEntry;
        Country countryEntry;

        if(alreadyExists) {
            throw new ContactAlreadyExistsException();
        } else {
            Integer countryId = null;
            Integer cityId = null;
            Integer addressId = null;
            Integer customerId = null;

            //First get the country ID figured out.
            if(Country.alreadyExists(country)) {
                countryEntry = Country.getCountry(country);
                countryId = countryEntry.getCountryId();
            } else {
                Country country1 = new Country();
                country1.setCountry(country);

                if(Country.createNewCountry(country1)){
                    countryEntry = country1;
                    countryId = country1.getCountryId();
                } else {
                    throw new IllegalStateException(country1.getCountry() + " does not exist in the database but was not successfully created.");
                }
            }

            //Next is the city ID
            if(City.alreadyExists(city, countryId)) {
                cityEntry = City.getCity(city, countryId);
                cityId = City.getCityId(city, countryId);
            } else {
                City city1 = new City();
                city1.setCity(city);
                city1.setCountryId(countryId);

                if(City.createNewCity(city1)) {
                    cityEntry = city1;
                    cityId = city1.getCityId();
                } else {
                    throw new IllegalStateException(city1.getCity() + " does not exist in the database but was not successfully created.");
                }
            }

            //Next is the address ID
            if(Address.alreadyExists(addressLine, addressLine2, cityId, postalCode, phoneNumber)) {
                addressEntry = Address.getAddress(addressLine, addressLine2, cityId, postalCode, phoneNumber);
                addressId = Address.getAddressId(addressLine, addressLine2, cityId, postalCode, phoneNumber);
            } else {
                Address address = new Address();
                address.setAddress(addressLine);
                address.setAddress2(addressLine2);
                address.setCityId(cityId);
                address.setPostalCode(postalCode);
                address.setPhone(phoneNumber);

                if(Address.createNewAddress(address)) {
                    addressEntry = address;
                    addressId = address.getAddressId();
                } else {
                    throw new IllegalStateException("The address being added does not exist in the database but was not successfully created.");
                }
            }

            //Finally we can get the user ID.
            if(Customer.alreadyExists(customerName, addressId)) {
               return null;
            } else {
                Customer customer = new Customer();
                customer.setCustomerName(customerName);
                customer.setAddressId(addressId);

                if(Customer.createNewCustomer(customer)) {
                    customerEntry = customer;
                    customerId = customer.getCustomerId();
                } else {
                    throw new IllegalStateException("The address being added does not exist in the database but was not successfully created.");
                }
            }

            record = new Record(customerEntry, addressEntry, cityEntry, countryEntry);
        }

        return record;
    }

    public static void updateExistingRecord(Record record, String fullName, String address, String address2, String city,
                                            String country, String postalCode, String phoneNumber) throws CountryCreationException, CityCreationException, AddressCreationException, CustomerUpdateException, CountEntriesException {

        Integer countryId;
        Integer cityId;
        Integer addressId;
        Integer customerId;

        boolean countryChanged = false;
        boolean cityChanged = false;
        boolean addressChanged = false;
        boolean customerChanged = false;

        //What changed
        if(!record.getCountry().getCountry().equals(country)) {

            if(!Country.alreadyExists(country)) {
                Country newCountry = new Country();
                newCountry.setCountry(country);

                if(Country.createNewCountry(newCountry)) {
                    countryId = Country.getCountryId(newCountry);
                    countryChanged = true;
                } else {
                    throw new CountryCreationException();
                }
            } else {
                countryId = Country.getCountryId(country);
            }
        } else {
            countryId = Country.getCountryId(country);
        }

        if(!record.getCity().getCity().equals(city) || !record.getCity().getCountryId().equals(countryId)) {

            if(!City.alreadyExists(city, countryId)) {
                City newCity = new City();
                newCity.setCity(city);
                newCity.setCountryId(countryId);

                if(City.createNewCity(newCity)) {
                    cityId = City.getCityId(newCity);
                    cityChanged = true;
                } else {
                    throw new CityCreationException();
                }
            } else {
                cityId = City.getCityId(city, countryId);
            }
        } else {
            cityId = City.getCityId(city, countryId);
        }

        if(!record.getAddress().getAddress().equals(address) || !record.getAddress().getAddress2().equals(address2) ||
            !record.getAddress().getCityId().equals(cityId) || !record.getAddress().getPostalCode().equals(postalCode) ||
                !record.getAddress().getPhone().equals(phoneNumber)) {

            if(!Address.alreadyExists(address, address2, cityId, postalCode, phoneNumber)) {
                Address newAddress = new Address();
                newAddress.setAddress(address);
                newAddress.setAddress2(address2);
                newAddress.setCityId(cityId);
                newAddress.setPostalCode(postalCode);
                newAddress.setPhone(phoneNumber);

                if(Address.createNewAddress(newAddress)) {
                    addressId = Address.getAddressId(newAddress);
                    addressChanged = true;
                } else {
                    throw new AddressCreationException();
                }
            } else {
                addressId = Address.getAddressId(address, address2, cityId, postalCode, phoneNumber);
            }
        } else {
            addressId = Address.getAddressId(address, address2, cityId, postalCode, phoneNumber);
        }

        if(!record.getCustomer().getCustomerName().equals(fullName) || !record.getCustomer().getAddressId().equals(addressId)) {

            Customer newCustomer = new Customer();
            newCustomer.setCustomerName(fullName);
            newCustomer.setAddressId(addressId);

            if(Customer.updateCustomer(record.getCustomer(), newCustomer)) {
                customerChanged = true;
            } else {
                throw new CustomerUpdateException();
            }

        }

        if(customerChanged) {
            int count = Customer.countAddressId(record.getCustomer().getAddressId());

            if(count < 1) {
                Address.deleteAddress(record.getCustomer().getAddressId());
            }
        }

        if(addressChanged) {
            int count = Address.countCityId(record.getAddress().getCityId());

            if(count < 1) {
                City.deleteCity(record.getAddress().getCityId());
            }
        }

        if(cityChanged) {
            int count = City.countCountryId(record.getCity().getCountryId());

            if(count < 1) {
                Country.deleteCountry(record.getCity().getCountryId());
            }
        }

    }

    public static void deleteRecord(Record record) throws CountEntriesException, CustomerDeletionException {
        Integer customerId = record.getCustomer().getCustomerId();
        Integer addressId = record.getAddress().getAddressId();
        Integer cityId = record.getCity().getCityId();
        Integer countryId = record.getCountry().getCountryId();

        if(Customer.deleteCustomer(customerId)) {

            int count = Customer.countAddressId(addressId);

            if(count < 1) {
                Address.deleteAddress(addressId);
            }

            count = Address.countCityId(cityId);

            if(count < 1) {
                City.deleteCity(cityId);
            }

            count = City.countCountryId(countryId);

            if(count < 1) {
                Country.deleteCountry(countryId);
            }

        } else {
            throw new CustomerDeletionException();
        }

    }

    public String getCustomerName() {
        return customerName;
    }

    public Country getCountry() {
        if(country == null) {
            Integer countryId = getCity().getCountryId();

            country = Country.getCountry(countryId);
        }

        return country;
    }

    public City getCity() {
        if(city == null) {
            Integer cityId = getAddress().getCityId();

            city = City.getCity(cityId);
        }

        return city;
    }

    public Address getAddress() {
        if(address == null) {
            Integer addressId = getCustomer().getAddressId();

            address = Address.getAddress(addressId);
        }

        return address;
    }

    public Customer getCustomer() {
        return customer;
    }
}
