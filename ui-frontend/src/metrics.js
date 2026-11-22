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

const qualityRatio = (responses, fallbacks, queries) => {
  const ratio =
    ((responses ?? 0) - (fallbacks ?? 0)) / Math.max(1, queries ?? 0);
  return Math.min(1, Math.max(0, ratio));
};

export const toQuality = (responses, fallbacks, queries) => {
  const percent =
    Math.round(qualityRatio(responses, fallbacks, queries) * 100 * 10 + Number.EPSILON) /
    10;
  return `${percent}%`;
};

export const toQualityRank = (responses, fallbacks, queries) => {
  const ratio = qualityRatio(responses, fallbacks, queries);
  if (ratio < 0.2) {
    return "failure";
  } else if (ratio < 0.6) {
    return "warning";
  }
  return "";
};
