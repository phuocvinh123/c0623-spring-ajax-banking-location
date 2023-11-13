package com.cg.model.dto.request;

import com.cg.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.Valid;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class CustomerUpReqDTO implements Validator {

    private String fullName;
    private String email;
    private String phone;

    @Valid
    private LocationRegionUpReqDTO locationRegion;

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CustomerUpReqDTO customerUpReqDTO = (CustomerUpReqDTO) o;

        String fullName = customerUpReqDTO.fullName;
//        String address = creReqDTO.locationRegion.getAddress();

        if (fullName.length() < 3) {
            errors.rejectValue("fullName", "fullName.length", "Tên phải có ít nhất là 3 ký tự");
        }

//        if (address.length() < 3) {
//            errors.rejectValue("locationRegion.address", "address.length", "Địa chỉ phải có ít nhất là 3 ký tự");
//        }
    }

    public Customer toCustomer(Long customerId) {
        return new Customer()
                .setId(customerId)
                .setFullName(fullName)
                .setPhone(phone)
                .setLocationRegion(locationRegion.toLocationRegion())
                ;
    }
}
