package xyz.thewhitedog9487.WebAPI.Data.Specification

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import xyz.thewhitedog9487.WebAPI.Data.Entity.AccessLog

object AccessLogSpecification {
    fun OrderByField(TargetFieldName: String, Order: Sort.Direction) = Specification<AccessLog>{
        Root, CriteriaQuery, CriteriaBuilder ->
        CriteriaQuery.orderBy(
            when(Order){
                Sort.Direction.ASC -> CriteriaBuilder.asc(Root.get<Any>(TargetFieldName))
                Sort.Direction.DESC -> CriteriaBuilder.desc(Root.get<Any>(TargetFieldName)) })
        return@Specification CriteriaBuilder.conjunction() } }