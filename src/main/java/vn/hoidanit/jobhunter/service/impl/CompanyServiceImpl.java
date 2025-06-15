package vn.hoidanit.jobhunter.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.service.CompanyService;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Company handleCreateCompany(Company company) {
        return companyRepository.save(company);
    }

    @Override
    public Company updateCompany(Company company) {
        Optional<Company> optionalCompany = this.companyRepository.findById(company.getId());
        if (optionalCompany.isPresent()) {
            Company upCom = optionalCompany.get();
            upCom.setName(company.getName());
            upCom.setDescription(company.getDescription());
            upCom.setAddress(company.getAddress());
            // @PreUpdate sẽ tự động set updatedAt và updatedBy
            return this.companyRepository.save(upCom);
        }
        return null;
    }

    @Override
    public void handelDeleteCompany(Long id) {
        this.companyRepository.deleteById(id);

    }

    @Override
    public ResultPaginationDTO getAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompanies = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageCompanies.getTotalPages());
        mt.setTotal(pageCompanies.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageCompanies.getContent());

        return rs;
    }

}
