package vn.hoidanit.jobhunter.service;

import java.util.List;

import vn.hoidanit.jobhunter.domain.Company;

public interface CompanyService {

    Company handleCreateCompany(Company company);

    List<Company> getAllCompanies();

}
