package vn.hoidanit.jobhunter.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;

public interface CompanyService {

    Company handleCreateCompany(Company company);

    // ResultPaginationDTO getAllCompanies(Pageable pageable);

    Company updateCompany(Company company);

    void handelDeleteCompany(Long id);

    ResultPaginationDTO getAllCompanies(Specification<Company> spec, Pageable pageable);

}
