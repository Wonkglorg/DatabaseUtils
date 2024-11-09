package com.wonkglorg.ip;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;

/**
 * Represents an IPv4
 */
@SuppressWarnings("unused")
public class IPv4 extends IP<IPv4> {

    public static final IPv4 Min = new IPv4(new int[]{0, 0, 0, 1});
    public static final IPv4 Max = new IPv4(new int[]{255, 255, 255, 255});
    private int[] ip = new int[4];

    private IPv4(int[] ipParts) {
        this.ip = ipParts;
    }

    /**
     * Creates a new ip from its 4 parts
     *
     * @param ip the 4 parts of the ip address
     * @throws MalformedIpException if the ip is not valid
     */
    public static IPv4 of(int[] ip) {
        var result = validate(ip);

        if (!result.isValid()) {
            throw result.getException();
        }

        return result.getIp();
    }

    /**
     * Creates a new ip from its 4 parts
     *
     * @param part1 first ip part
     * @param part2 second ip part
     * @param part3 third ip part
     * @param part4 fourth ip part
     * @throws MalformedIpException if the ip has not 4 parts or a part is not between 0 and 255
     */
    public static IPv4 of(int part1, int part2, int part3, int part4) {
        return of(new int[]{part1, part2, part3, part4});
    }

    /**
     * Creates a new ip from a string
     *
     * @param ip the string ip representation formatted as "xxx.xxx.xxx.xxx"
     * @throws IllegalArgumentException if the ip is not in the correct format
     */
    public static IPv4 of(String ip) {
        var result = validate(ip);
        if (!result.isValid()) {
            throw result.getException();
        }
        return result.getIp();
    }


    /**
     * Validated an ipv4 address formatted as "xxx.xxx.xxx.xxx"
     *
     * @param ip the ip string to test
     * @return an empty optional if valid else the error it threw
     */
    private static ValidationResult<IPv4> validate(String ip) {
        if (ip == null || ip.isBlank()) {
            return new ValidationResult<>("IP cannot be null or blank");
        }

        if (ip.chars().filter(ch -> ch == ':').count() > 3) {
            return new ValidationResult<>("Malformed Ip");
        }

        String[] parts = ip.split("\\.",-1);

        if (parts.length != 4) {
            return new ValidationResult<>("IP must have 4 parts but found " + parts.length);
        }

        int[] ipArray = new int[4];
        try {
            for (int i = 0; i < 4; i++) {
                ipArray[i] = Integer.parseInt(parts[i]);
            }
        } catch (NumberFormatException e) {
            return new ValidationResult<>(new MalformedIpException(e));
        }

        return validate(ipArray);
    }

    /**
     * Validates an ip based on its 4 int parts
     *
     * @return an empty optional if valid else the error it threw
     */
    private static ValidationResult<IPv4> validate(int[] ipParts) {
        if (ipParts.length != 4) {
            return new ValidationResult<>("IP must have 4 parts but found " + ipParts.length);
        }

        for (int i = 0; i < 4; i++) {
            if (ipParts[i] < 0 || ipParts[i] > 255) {
                return new ValidationResult<>(
                        "IP parts must be between 0 and 255 but was " + ipParts[i] + " at position " + i);
            }
        }

        return new ValidationResult<>(new IPv4(ipParts));
    }


    /**
     * Returns the ip as an array of integers
     *
     * @param subnetMask the subnet mask
     * @return the network address
     */
    public IPv4 getNetworkAddress(IPv4 subnetMask) {
        return IPv4.getNetworkAddress(this, subnetMask);
    }

    /**
     * Returns the network address of the ip
     *
     * @param cidr mask from cidr notation
     * @return the broadcast address
     */
    public IPv4 getNetworkAddress(int cidr) {
        return getNetworkAddress(this, cidr);
    }


    /**
     * Returns the broadcast address of the ip
     *
     * @param subnetMask the subnet mask
     * @return the broadcast address
     */
    public IPv4 getBroadcastAddress(IPv4 subnetMask) {
        return IPv4.getBroadcastAddress(this, subnetMask);
    }

    /**
     * Returns the broadcast address of the ip
     *
     * @param cidr mask from cidr notation
     * @return the broadcast address
     */
    public IPv4 getBroadcastAddress(int cidr) {
        IPv4 subnetMask = IPv4.maskFromCidr(cidr);
        return getBroadcastAddress(this, subnetMask);
    }

    /**
     * Returns the network address
     *
     * @param ip         the ip
     * @param subnetMask the subnet mask
     * @return the network address
     */
    public static IPv4 getNetworkAddress(IPv4 ip, IPv4 subnetMask) {
        return calculateAddress(ip, subnetMask, (a, b) -> a & b);
    }

    /**
     * Returns the network address of the ip
     *
     * @param cidr mask from cidr notation
     * @return the broadcast address
     */
    public static IPv4 getNetworkAddress(IPv4 ip, int cidr) {
        IPv4 subnetMask = IPv4.maskFromCidr(cidr);
        return getNetworkAddress(ip, subnetMask);
    }


    /**
     * Returns the broadcast address
     *
     * @param ip         the ip
     * @param subnetMask the subnet mask
     * @return the broadcast address
     */
    public static IPv4 getBroadcastAddress(IPv4 ip, IPv4 subnetMask) {
        return calculateAddress(ip, subnetMask, (a, b) -> a | ~b & 0xFF);
    }

    /**
     * Returns the broadcast address of the ip
     *
     * @param cidr mask from cidr notation
     * @return the broadcast address
     */
    public static IPv4 getBroadcastAddress(IPv4 iPv4, int cidr) {
        IPv4 subnetMask = IPv4.maskFromCidr(cidr);
        return getBroadcastAddress(iPv4, subnetMask);
    }

    /**
     * Executes an operation
     *
     * @param ip         the initial {@link IPv4}
     * @param subnetMask the second {@link IPv4}
     * @param operator   the operation to do
     * @return the new Calculated {@link IPv4}
     */
    private static IPv4 calculateAddress(IPv4 ip, IPv4 subnetMask, IntBinaryOperator operator) {
        Objects.requireNonNull(subnetMask);
        Objects.requireNonNull(ip);
        Objects.requireNonNull(operator);
        int[] result = new int[4];
        for (int i = 0; i < 4; i++) {
            result[i] = operator.applyAsInt(ip.ip[i], subnetMask.ip[i]);
        }
        return new IPv4(result);
    }

    public static IPv4 maskFromCidr(int cidr) {
        validateCIDR(cidr).ifPresent(e -> {
            throw e;
        });
        int mask = 0xFFFFFFFF << (32 - cidr);
        return IPv4.of((mask >> 24) & 0xFF, (mask >> 16) & 0xFF, (mask >> 8) & 0xFF, mask & 0xFF);
    }


    public static Optional<IllegalArgumentException> validateCIDR(int cidr) {
        if (cidr < 0 || cidr > 32) {
            return Optional.of(
                    new IllegalArgumentException("CIDR value must be between 0 and 32 but was " + cidr));
        }
        return Optional.empty();
    }

    /**
     * Converts the Ipv4 Address to a valid Ipv6 Address formatted as ::ffff:IPv4-address
     */
    public IPv6 toIpv6() {
        return IPv6.fromIpv4(this);
    }

    /**
     * @return a copy of the ipv4 segments
     */
    public int[] getSegments() {
        return Arrays.copyOf(ip, ip.length);
    }


    /**
     * Returns the ip as a string formatted as "xxx.xxx.xxx.xxx"
     */
    @Override
    public String toString() {
        return Arrays.stream(ip)//
                .mapToObj(String::valueOf)//
                .collect(Collectors.joining("."));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IPv4 ipv4 = (IPv4) o;
        return Arrays.equals(ip, ipv4.ip);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(ip);
    }
}
