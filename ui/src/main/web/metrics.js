export const toNumberWithUnit = (num) => {
  if (typeof num !== "number" || num < 1) {
    return "0";
  } else if (num > 99999999999999999999) {
    return ">100E";
  }
  let i = Math.floor(Math.log(num) / Math.log(1000));
  let q = 10 ** Math.min(3, i);
  let value = Math.round((num / 1000 ** i + Number.EPSILON) * q) / q;
  let suffix = ["", "k", "M", "G", "T", "P", "E"][i];
  return `${value}${suffix}`;
};

export const toMillis = (ms) => {
  return `${Math.round(ms * 10 + Number.EPSILON) / 10}ms`;
};

export const toThroughputPerSecond = (d) => {
  let ps = Math.round(d * 10 + Number.EPSILON) / 10;
  if (ps < 1.0) {
    return `${Math.round(d * 60 * 10 + Number.EPSILON) / 10}/min`;
  }
  return `${ps}/s`;
};

export const toPercent = (n, d) => {
  return `${
    Math.round((n / Math.max(1, d)) * 100 * 10 + Number.EPSILON) / 10
  }%`;
};

export const toRateRank = (n, d) => {
  let rate = n / Math.max(1, d);
  if (rate < 0.2) {
    return "failure";
  } else if (rate < 0.6) {
    return "warning";
  }
};
