package com.healthstore.dto.mapper;

import com.healthstore.dto.AddressDTO;
import com.healthstore.model.Address;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Address and AddressDTO objects.
 */
@Component
public class AddressMapper {

    /**
     * Converts an Address entity to an AddressDTO.
     *
     * @param address The Address entity to convert.
     * @return The corresponding AddressDTO.
     */
    public AddressDTO toDto(Address address) {
        if (address == null) {
            return null;
        }

        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setPostalCode(address.getPostalCode());
        dto.setCountry(address.getCountry());
        dto.setPhoneNumber(address.getPhoneNumber());
        dto.setDefault(address.isDefault());
        dto.setAddressType(address.getAddressType());
        
        // Set user ID if user is not null
        if (address.getUser() != null) {
            dto.setUserId(address.getUser().getId());
        }
        
        return dto;
    }

    /**
     * Converts an AddressDTO to an Address entity.
     *
     * @param dto The AddressDTO to convert.
     * @return The corresponding Address entity.
     */
    public Address toEntity(AddressDTO dto) {
        if (dto == null) {
            return null;
        }

        Address address = new Address();
        address.setId(dto.getId());
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());
        address.setPhoneNumber(dto.getPhoneNumber());
        address.setDefault(dto.isDefault());
        address.setAddressType(dto.getAddressType());
        
        // Note: User must be set separately as it's not included in the DTO
        
        return address;
    }
}
