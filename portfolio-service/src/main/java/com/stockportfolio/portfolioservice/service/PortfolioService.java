package com.stockportfolio.portfolioservice.service;

import com.stockportfolio.portfolioservice.client.PriceFetcherClient;
import com.stockportfolio.portfolioservice.dto.HoldingRequest;
import com.stockportfolio.portfolioservice.dto.HoldingResponse;
import com.stockportfolio.portfolioservice.dto.PortfolioRequest;
import com.stockportfolio.portfolioservice.dto.PortfolioResponse;
import com.stockportfolio.portfolioservice.entity.Holding;
import com.stockportfolio.portfolioservice.entity.Portfolio;
import com.stockportfolio.portfolioservice.exception.ResourceNotFoundException;
import com.stockportfolio.portfolioservice.repository.HoldingRepository;
import com.stockportfolio.portfolioservice.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final PriceFetcherClient priceFetcherClient;

    public List<PortfolioResponse> getPortfoliosByUserId(Long userId) {
        return portfolioRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PortfolioResponse getPortfolioById(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + id));
        return mapToResponse(portfolio);
    }

    @Transactional
    public PortfolioResponse createPortfolio(PortfolioRequest request, Long userId) {
        Portfolio portfolio = Portfolio.builder()
                .name(request.getName())
                .description(request.getDescription())
                .userId(userId)
                .build();
        return mapToResponse(portfolioRepository.save(portfolio));
    }

    @Transactional
    public PortfolioResponse updatePortfolio(Long id, PortfolioRequest request) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + id));
        portfolio.setName(request.getName());
        portfolio.setDescription(request.getDescription());
        return mapToResponse(portfolioRepository.save(portfolio));
    }

    @Transactional
    public void deletePortfolio(Long id) {
        if (!portfolioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Portfolio not found with id: " + id);
        }
        portfolioRepository.deleteById(id);
    }

    public List<HoldingResponse> getHoldingsByPortfolioId(Long portfolioId) {
        return holdingRepository.findByPortfolioId(portfolioId)
                .stream()
                .map(this::mapHoldingToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public HoldingResponse addHolding(HoldingRequest request) {
        Portfolio portfolio = portfolioRepository.findById(request.getPortfolioId())
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + request.getPortfolioId()));

        Holding holding = Holding.builder()
                .stockSymbol(request.getStockSymbol().toUpperCase())
                .stockName(request.getStockName())
                .quantity(request.getQuantity())
                .buyPrice(request.getBuyPrice())
                .portfolio(portfolio)
                .build();

        return mapHoldingToResponse(holdingRepository.save(holding));
    }

    @Transactional
    public HoldingResponse updateHolding(Long id, HoldingRequest request) {
        Holding holding = holdingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holding not found with id: " + id));

        holding.setStockSymbol(request.getStockSymbol().toUpperCase());
        holding.setStockName(request.getStockName());
        holding.setQuantity(request.getQuantity());
        holding.setBuyPrice(request.getBuyPrice());

        return mapHoldingToResponse(holdingRepository.save(holding));
    }

    @Transactional
    public void deleteHolding(Long id) {
        if (!holdingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Holding not found with id: " + id);
        }
        holdingRepository.deleteById(id);
    }

    private PortfolioResponse mapToResponse(Portfolio portfolio) {
        List<HoldingResponse> holdingResponses = portfolio.getHoldings()
                .stream()
                .map(this::mapHoldingToResponse)
                .collect(Collectors.toList());

        BigDecimal totalInvested = holdingResponses.stream()
                .map(HoldingResponse::getInvestedValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCurrent = holdingResponses.stream()
                .map(HoldingResponse::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGainLoss = totalCurrent.subtract(totalInvested);
        BigDecimal totalGainLossPct = totalInvested.compareTo(BigDecimal.ZERO) != 0
                ? totalGainLoss.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return PortfolioResponse.builder()
                .id(portfolio.getId())
                .name(portfolio.getName())
                .description(portfolio.getDescription())
                .userId(portfolio.getUserId())
                .holdings(holdingResponses)
                .totalInvestedValue(totalInvested)
                .totalCurrentValue(totalCurrent)
                .totalGainLoss(totalGainLoss)
                .totalGainLossPercentage(totalGainLossPct.setScale(2, RoundingMode.HALF_UP))
                .createdAt(portfolio.getCreatedAt())
                .build();
    }

    private HoldingResponse mapHoldingToResponse(Holding holding) {
        BigDecimal currentPrice = fetchCurrentPrice(holding.getStockSymbol());
        BigDecimal investedValue = holding.getBuyPrice().multiply(BigDecimal.valueOf(holding.getQuantity()));
        BigDecimal currentValue = currentPrice.multiply(BigDecimal.valueOf(holding.getQuantity()));
        BigDecimal gainLoss = currentValue.subtract(investedValue);
        BigDecimal gainLossPct = investedValue.compareTo(BigDecimal.ZERO) != 0
                ? gainLoss.divide(investedValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return HoldingResponse.builder()
                .id(holding.getId())
                .stockSymbol(holding.getStockSymbol())
                .stockName(holding.getStockName())
                .quantity(holding.getQuantity())
                .buyPrice(holding.getBuyPrice())
                .currentPrice(currentPrice)
                .investedValue(investedValue.setScale(2, RoundingMode.HALF_UP))
                .currentValue(currentValue.setScale(2, RoundingMode.HALF_UP))
                .gainLoss(gainLoss.setScale(2, RoundingMode.HALF_UP))
                .gainLossPercentage(gainLossPct.setScale(2, RoundingMode.HALF_UP))
                .createdAt(holding.getCreatedAt())
                .build();
    }

    private BigDecimal fetchCurrentPrice(String symbol) {
        try {
            return priceFetcherClient.getCurrentPrice(symbol);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
