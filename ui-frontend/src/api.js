export const publishQuery = async (query) => {
  let timeout = 5000;
  
  const parts = query.split(" ");
  if (parts.length > 1) {
    timeout = Math.max(timeout, Number.parseInt(parts[1]) + 2000);
  }

  return await fetch("/api/v1?q=" + query, {
    signal: AbortSignal.timeout(timeout),
  });
};
