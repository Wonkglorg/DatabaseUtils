package com.wonkglorg.ip;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an IPv6
 */
@SuppressWarnings("unused")
public class IPv6 extends IP<IPv6> {
    public static final IPv6 Min =
            new IPv6(new String[]{"0000", "0000", "0000", "0000", "0000", "0000", "0000", "0000"});
    public static final IPv6 Max =
            new IPv6(new String[]{"ffff", "ffff", "ffff", "ffff", "ffff", "ffff", "ffff", "ffff"});
    private String[] ip = new String[8];
    private String ipShortenedText = null;
    private final String ipText;


    private IPv6(String[] ip) {
        List<String> ipParts = Arrays.stream(ip).map(String::toLowerCase).toList();
        this.ip = ipParts.toArray(new String[0]);
        this.ipText = String.join(":", ipParts);
    }


    /**
     * Creates a new ip from a string formatted as any valid IPv6
     *
     * @param ip the ip to convert
     * @throws IllegalArgumentException if the ip is not in the correct format
     */
    public static IPv6 of(String ip) {
        var result = validate(ip);
        if (!result.isValid()) {
            throw result.getException();
        }
        return result.getIp();
    }

    /**
     * Creates a new ip from its 8 parts (an empty part is equal to ::) where of("","ffff") is equal
     * to ::ffff
     *
     * @throws IllegalArgumentException if the ip has not 8 parts or a part is not valid
     */
    public static IPv6 of(String part1, String part2, String part3, String part4, String part5,
                          String part6, String part7, String part8) {
        return of(new String[]{part1, part2, part3, part4, part5, part6, part7, part8});
    }

    /**
     * Creates a new ip from its 8 parts (an empty part is equal to ::) where of("","ffff") is equal
     * to ::ffff
     *
     * @param ip parts of the ip
     * @throws IllegalArgumentException if the ip has not 8 parts or a part is not valid
     */
    public static IPv6 of(String[] ip) {
        var result = validate(ip);
        if (!result.isValid()) {
            throw result.getException();
        }
        return result.getIp();
    }


    /**
     * Checks if the ip has a valid format
     *
     * @param ip the ip to check
     * @return an empty optional of the ip is valid the exception otherwise
     */
    public static ValidationResult<IPv6> validate(String ip) {
        if (ip == null || ip.isBlank()) {
            return new ValidationResult<>("Ip must not be null or empty");
        }
        int count = (int) ip.chars().filter(ch -> ch == ':').count();

        //use own split method the default one omits trailing spaces
        String[] parts = ip.split(":");

        //special case for :: ip
        boolean isEmpty = true;
        if (parts.length == 3 && count == 2) {
            for (String part : parts) {
                if (!part.isEmpty()) {
                    isEmpty = false;
                    break;
                }
            }
            if (isEmpty) {
                return validate(IPv6.Min.ip);
            }
        }

        if (parts.length > 2) {
            if (parts[0].isEmpty() && parts[1].isEmpty()) {
                parts = Arrays.copyOfRange(parts, 1, parts.length);
            } else if (parts[parts.length - 2].isEmpty() && parts[parts.length - 1].isEmpty()) {
                parts = Arrays.copyOfRange(parts, 0, parts.length - 1);
            }
        }

        return validate(parts);
    }


    private static ValidationResult<IPv6> validate(String[] parts) {
        if (parts == null) {
            return new ValidationResult<>("Ip must not be null or empty");
        }
        if (parts.length <= 1) {
            return new ValidationResult<>("Invalid IP format reason: at least 2 parts are needed");
        }

        if (parts.length > 8) {
            return new ValidationResult<>("Invalid IP format reason: too many parts");
        }

        boolean hasDoubleColon = false;
        int doubleColonIndex = -1;


        //checks if only one double colon is present
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) {
                if (hasDoubleColon) {
                    return new ValidationResult<>(
                            "Multiple instances of shortened IP parts found only 1 is allowed per address, at "
                                    + "index: " + i);
                } else {
                    hasDoubleColon = true;
                    doubleColonIndex = i;
                }
            }
            String segmentResult = isValidSegment(part);
            if (segmentResult != null) {
                return new ValidationResult<>(segmentResult);
            }
        }

        parts = expandShortenedAddress(parts, doubleColonIndex, hasDoubleColon);

        //if non is present make sure it has 8 parts
        if (!hasDoubleColon && parts.length != 8) {
            return new ValidationResult<>(
                    "Invalid IP format reason: exactly 8 parts are required if no double colon is "
                            + "present");
        }


        return new ValidationResult<>(new IPv6(parts));
    }

    //todo:jmd implement network address calculations

    /**
     * Ip should be validated beforehand
     *
     * @param parts ip address parts
     * @return expanded parts (resolving :: to its 0 forms and prepending any non 4 char parts with
     * 0's)
     */
    private static String[] expandShortenedAddress(String[] parts, int doubleColonIndex,
                                                   boolean hasDoubleColon) {
        // expands ::
        if (hasDoubleColon) {
            //check how many are missing
            int missingZeros = 8 - parts.length + 1;
            String[] expandedParts = new String[8];
            int currentIndex = 0;

            // copy before ::
            for (int i = 0; i < doubleColonIndex; i++) {
                expandedParts[currentIndex++] = parts[i];
            }

            //insert 0's
            for (int i = 0; i < missingZeros; i++) {
                expandedParts[currentIndex++] = "0000";
            }

            //copy after "::"
            for (int i = doubleColonIndex + 1; i < parts.length; i++) {
                expandedParts[currentIndex++] = parts[i];
            }

            parts = expandedParts;
        }

        //expands other parts to 4
        for (int i = 0; i < parts.length; i++) {
            parts[i] =
                    String.format("%4s", parts[i]).replace(' ', '0'); // Ensure each part is 4 digits long
        }
        return parts;
    }


    /**
     * Checks if the segment is valid
     *
     * @param part segment to check
     * @return an empty optional if no error occured otherwise the error to throw
     */
    private static String isValidSegment(String part) {
        if (part.length() > 4) {
            return part + " cannot have more than 4 digits";
        }
        for (char c : part.toCharArray()) {
            if (!Character.isDigit(c) && (c < 'a' || c > 'f') && (c < 'A' || c > 'F')) {
                return c + " is not a valid hexadecimal character";
            }
        }
        return null;
    }

    public String toShortenedIP() {
        if (ipShortenedText == null) {
            ipShortenedText = createShortenedIp();
        }
        return ipShortenedText;
    }

    /**
     * @return returns a shortened IPv6 with leading zeros removed and the longest sequence of zeros
     * shortened to "::"
     */
    private String createShortenedIp() {
        String[] ipParts = ip;
        int longestZeroSequence = 0;
        int currentZeroSequence = 0;
        int longestZeroSequenceIndex = -1;
        int currentZeroSequenceIndex = -1;

        //removes leading zeros
        ipParts = Arrays.stream(ipParts).map(part -> part.replaceAll("^0+", ""))
                .map(part -> part.isEmpty() ? "0" : part) //if all 4 digits are 0 append a 0 to not leave it
                // empty
                .toArray(String[]::new);

        //longest sequence of just 0's
        for (int i = 0; i < ipParts.length; i++) {
            if ("0".equals(ipParts[i])) {
                if (currentZeroSequence == 0) {
                    currentZeroSequenceIndex = i;
                }
                currentZeroSequence++;

                if (currentZeroSequence > longestZeroSequence) {
                    longestZeroSequence = currentZeroSequence;
                    longestZeroSequenceIndex = currentZeroSequenceIndex;
                }
            } else {
                currentZeroSequence = 0;
            }
        }

        //compress the longest part down to just ::
        StringBuilder sb = new StringBuilder();
        boolean hasCompressed = false;

        for (int i = 0; i < ipParts.length; i++) {
            if (i == longestZeroSequenceIndex && longestZeroSequence > 1 && !hasCompressed) {
                sb.append("::");
                hasCompressed = true;
                i += longestZeroSequence - 1;
            } else {
                sb.append(ipParts[i]);
                if (i < ipParts.length - 1) {
                    sb.append(":");
                }
            }
        }

        //special case if it ends with :: it appends an extra : this removes it again
        if (sb.toString().endsWith(":::") && hasCompressed) {
            sb.delete(sb.length() - 1, sb.length());
        }

        return sb.toString();
    }

    /**
     * Converts the Ipv4 Address to a valid Ipv6 Address formatted as ::ffff:IPv4-address
     */
    public static IPv6 fromIpv4(IPv4 iPv4) {
        String ipString = Arrays.stream(iPv4.getSegments())//
                .mapToObj(String::valueOf)//
                .collect(Collectors.joining(":"));
        return IPv6.of("::ffff:" + ipString);
    }

    /**
     * @return ip formatted as "xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx"
     */
    @Override
    public String toString() {
        return ipText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IPv6 ipv6 = (IPv6) o;
        return Arrays.equals(ip, ipv6.ip);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(ip);
    }
}
