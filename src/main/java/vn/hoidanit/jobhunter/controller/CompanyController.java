package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.res.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")

public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("companies")
    public ResponseEntity<?> createCompany(@Valid @RequestBody Company company) {
        Company com = this.companyService.handleCreateCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(com);
    }

    @GetMapping("companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(@Filter Specification<Company> spec, Pageable pageable) {

        ResultPaginationDTO com = this.companyService.getAllCompanies(spec, pageable);
        return ResponseEntity.ok().body(com);
    }

    @PutMapping("companies")
    public ResponseEntity<?> updateCompany(@RequestBody Company company) {
        Company upCom = this.companyService.updateCompany(company);
        if (upCom == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(upCom);
    }

    @DeleteMapping("companies/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable("id") Long id) {
        this.companyService.handelDeleteCompany(id);
        return ResponseEntity.ok().body("Successfully");
    }
}
